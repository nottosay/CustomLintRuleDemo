package com.tal.xes.lint.lintrule.detectors

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*
import java.util.*


class IndexOutOfBoundsDetector : Detector(), Detector.UastScanner {


    companion object {
        private val ISSUE_ID = "IndexOutOfBoundsError"
        private val ISSUE_DESCRIPTION = "有可能出现数组越界异常"
        private val ISSUE_EXPLANATION = "有可能出现数组越界异常. 请对数组下标进行越界判断活着添加try/catch.如果你已经添加,请格式化代码."

        val ISSUE = Issue.create(
                ISSUE_ID,
                ISSUE_DESCRIPTION,
                ISSUE_EXPLANATION,
                Category.CORRECTNESS,
                9,
                Severity.FATAL,
                Implementation(
                        IndexOutOfBoundsDetector::class.java,
                        Scope.JAVA_FILE_SCOPE
                )
        )

        private const val ARRAYLIST_CLS = "java.util.ArrayList"
        private const val LIST_CLS = "java.util.List"
    }

    override fun getApplicableMethodNames(): MutableList<String> {
        return Arrays.asList(
                "get")
    }

    override fun visitMethod(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethod(context, node, method)
        val evaluator = context.evaluator
        if (!evaluator.isMemberInClass(method, LIST_CLS) && !evaluator.isMemberInClass(method, ARRAYLIST_CLS)) {
            return;
        }
        val receiver = node.receiver
        val parent = hasIf(node.uastParent)
        if (parent != null && parent is UIfExpression) {
            val argument = node.valueArguments[0]
            if (parent.condition.toString().contains("$receiver.size() > 0")
                    && parent.condition.toString().contains("$receiver.size() > $argument")) {
                return
            }
        }

        if (parent != null && parent is UTryExpression) {
            for (uc in parent.catchClauses){
                if(uc.typeReferences.toString().contains("java.lang.Exception")|| uc.typeReferences.toString().contains("java.lang.ArrayIndexOutOfBoundsException")){
                    return
                }
            }
        }

        val location = context.getLocation(node)
        context.report(IndexOutOfBoundsDetector.ISSUE, node, location, IndexOutOfBoundsDetector.ISSUE_DESCRIPTION)
    }

    private fun hasIf(element: UElement?): UElement? {
        if (element is UIfExpression) {
            return element
        } else if (element is UTryExpression) {
            return element
        }else if (element is UMethod) {
            return null
        } else {
            return hasIf(element?.uastParent)
        }
    }
}