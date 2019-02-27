package com.tal.xes.lint.lintrule.detectors

import com.android.annotations.NonNull
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.uast.UCallExpression
import java.util.*


/**
 * activity布局文件命名规则
 */
class FragmentLayoutNameDetector : Detector(), Detector.UastScanner {

    override fun visitMethod(context: JavaContext?, node: UCallExpression, method: PsiMethod?) {
        super.visitMethod(context, node, method)
        if (!isInstanceOfFragment(method)) {
            return
        }
        if (isInflateCalledInOnCreateView(node)) {

            val layoutString = getParaWithLayoutResourceId(node, method)

            if ("" == layoutString) {
                return
            }

            if (!isFileStringStartWithPrefix(layoutString, "fragment_")) {
                context?.report(ISSUE,
                        node,
                        context.getLocation(node),
                        ISSUE_ACTIVITY_DESCRIPTION)
            }
        }
    }


    override fun getApplicableMethodNames(): List<String>? {
        return Arrays.asList("inflate")
    }

    private fun isInstanceOfFragment(method: PsiMethod?): Boolean {
        val surroundingClass = method?.containingClass
        var parentClass = surroundingClass?.superClass
        while (parentClass != null) {
            if ("android.app.Fragment".equals(parentClass.qualifiedName) || "android.support.v4.app.Fragment".equals(parentClass.qualifiedName)) {
                return true
            } else {
                parentClass = parentClass.superClass
            }
        }
        return false
    }


    private fun isInflateCalledInOnCreateView(@NonNull node: UCallExpression?): Boolean {
        val surroundingDeclaration = PsiTreeUtil.getParentOfType(node?.psi, PsiMethod::class.java, false)
        if ("onCreateView".equals(surroundingDeclaration?.name)) {
            return true
        }
        return false
    }


    private fun isFileStringStartWithPrefix(layoutFileResourceString: String, prefix: String): Boolean {
        val lastDotIndex = layoutFileResourceString.lastIndexOf(".")
        val fileName = layoutFileResourceString.substring(lastDotIndex + 1)
        return fileName.startsWith(prefix)
    }


    private fun getParaWithLayoutResourceId(node: UCallExpression?, method: PsiMethod?): String {
        val arguments = node?.valueArguments?.iterator()
        val argument = arguments?.next()
        if (method != null) {
            val type = method.parameterList.parameters[0].type
            if ("int".equals(type.canonicalText)) {
                return argument.toString()
            }
        }

        return ""
    }

    companion object {

        private val ISSUE_ACTIVITY_ID = "布局文件命名不规范"
        private val ISSUE_ACTIVITY_DESCRIPTION = "fragment布局文件必须以{fragment_}开头"
        private val ISSUE_ACTIVITY_EXPLANATION = "fragment布局文件必须以{fragment_}开头. 例如, `fragment_test.xml`."

        val ISSUE = Issue.create(
                ISSUE_ACTIVITY_ID,
                ISSUE_ACTIVITY_DESCRIPTION,
                ISSUE_ACTIVITY_EXPLANATION,
                Category.MESSAGES,
                9,
                Severity.FATAL,
                Implementation(FragmentLayoutNameDetector::class.java,
                        Scope.JAVA_FILE_SCOPE)
        )
    }
}
