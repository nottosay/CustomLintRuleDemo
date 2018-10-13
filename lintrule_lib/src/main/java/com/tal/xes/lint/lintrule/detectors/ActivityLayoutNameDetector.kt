package com.tal.xes.lint.lintrule.detectors

import com.android.annotations.NonNull
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

/**
 * activity布局文件命名规则
 */
class ActivityLayoutNameDetector : Detector(), Detector.UastScanner {

    override fun visitMethod(context: JavaContext?, node: UCallExpression?, method: PsiMethod?) {
        super.visitMethod(context, node, method)
        if (isActivitySetContentView(node) && isInstanceOfActivity(context, method)) {
            val arguments = node?.valueArguments?.iterator()
            val argument = arguments?.next() ?: return
            val layoutString = argument.toString()

            if (!isFileStringStartWithPrefix(layoutString, "activity_")) {
                context?.report(ISSUE,
                        node,
                        context.getLocation(node),
                        ISSUE_ACTIVITY_DESCRIPTION)
            }
        }
    }


    override fun getApplicableMethodNames(): List<String>? {
        return Arrays.asList("setContentView")
    }


    private fun isActivitySetContentView(@NonNull node: UCallExpression?): Boolean {
        val argOwner = node?.psi?.node?.text
        return argOwner!!.startsWith("setContentView(") || argOwner.startsWith("this.setContentView(")
    }

    private fun isInstanceOfActivity(@NonNull context: JavaContext?, @NonNull method: PsiMethod?): Boolean {
        var surroundingClass = method?.containingClass

        while (surroundingClass != null) {
            if (surroundingClass.qualifiedName!!.contains("android.app.Activity") || surroundingClass.qualifiedName!!.contains("android.support.v7.app.AppCompatActivity")) {
                return true
            } else {
                surroundingClass = surroundingClass.superClass
            }
        }
        return false
    }

    private fun isFileStringStartWithPrefix(layoutFileResourceString: String, prefix: String): Boolean {
        val lastDotIndex = layoutFileResourceString.lastIndexOf(".")
        val fileName = layoutFileResourceString.substring(lastDotIndex + 1)
        return fileName.startsWith(prefix)
    }

    companion object {

        private val ISSUE_ACTIVITY_ID = "布局文件命名不规范"
        private val ISSUE_ACTIVITY_DESCRIPTION = "activity布局文件必须以{activity_}开头"
        private val ISSUE_ACTIVITY_EXPLANATION = "activity布局文件必须以{activity_}开头. 例如, `activity_main.xml`."

        val ISSUE = Issue.create(
                ISSUE_ACTIVITY_ID,
                ISSUE_ACTIVITY_DESCRIPTION,
                ISSUE_ACTIVITY_EXPLANATION,
                Category.MESSAGES,
                9,
                Severity.FATAL,
                Implementation(ActivityLayoutNameDetector::class.java,
                        Scope.JAVA_FILE_SCOPE)
        )
    }
}
