package com.tal.xes.lint.lintrule.detectors

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*
import java.util.*


class NumberFormatDetector : Detector(), Detector.UastScanner {


    companion object {
        private val ISSUE_ID = "IndexOutOfBoundsError"
        private val ISSUE_DESCRIPTION = "有可能出现类型转换异常"
        private val ISSUE_EXPLANATION = "有可能出现类型转换异常. 请对类型转换添加try/catch.如果你已经添加,请格式化代码."

        val ISSUE = Issue.create(
                ISSUE_ID,
                ISSUE_DESCRIPTION,
                ISSUE_EXPLANATION,
                Category.CORRECTNESS,
                9,
                Severity.FATAL,
                Implementation(
                        NumberFormatDetector::class.java,
                        Scope.JAVA_FILE_SCOPE
                )
        )

        private const val INTEGER_CLS = "java.lang.Integer"
        private const val FLOAT_CLS = "java.lang.Float"
        private const val DOUBLE_CLS = "java.lang.Double"
    }

    override fun getApplicableMethodNames(): MutableList<String> {
        return Arrays.asList(
                "parseInt","parseFloat","parseDouble")
    }

    override fun visitMethod(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethod(context, node, method)
        val evaluator = context.evaluator
        if (!evaluator.isMemberInClass(method, INTEGER_CLS) && !evaluator.isMemberInClass(method, FLOAT_CLS) && !evaluator.isMemberInClass(method, DOUBLE_CLS)) {
            return;
        }
        val parent = hasTy(node.uastParent)
        if (parent != null && parent is UTryExpression) {
            for (uc in parent.catchClauses){
                if(uc.typeReferences.toString().contains("java.lang.Exception")|| uc.typeReferences.toString().contains("java.lang.NumberFormatException")){
                    return
                }
            }
        }

        val location = context.getLocation(node)
        context.report(NumberFormatDetector.ISSUE, node, location, NumberFormatDetector.ISSUE_DESCRIPTION)
    }

   private fun hasTy(element: UElement?): UElement? {
        if (element is UTryExpression) {
            return element
        }else if (element is UMethod) {
            return null
        } else {
            return hasTy(element?.uastParent)
        }
    }
}