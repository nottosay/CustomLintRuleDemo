package com.tal.xes.lint.lintrule.detectors

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

/**
 * log工具使用规则
 */
class LogDetector : Detector(), Detector.UastScanner {

    override fun visitMethod(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator
        if (!evaluator.isMemberInClass(method, LOG_CLS) && !evaluator.isMemberInClass(method, SYSTEM_CLS)) {
            return
        }

        val name = method.name

        if ("i" == name || "d" == name || "v" == name || PRINT == name || PRINTLN == name) {
            val location = context.getLocation(node)
            context.report(ISSUE, node, location, ISSUE_DESCRIPTION)
        }

    }

    override fun getApplicableMethodNames(): List<String>? =
            Arrays.asList(
                    "d",
                    "e",
                    "i",
                    "v",
                    "w",
                    PRINT,
                    PRINTLN)


    companion object {
        private val ISSUE_ID = "LogUseError"
        private val ISSUE_DESCRIPTION = "Log打印不符合规范"
        private val ISSUE_EXPLANATION = "Log打印不符合规范. 请使用自定义的LogUtil工具."

        val ISSUE = Issue.create(
                ISSUE_ID,
                ISSUE_DESCRIPTION,
                ISSUE_EXPLANATION,
                Category.CORRECTNESS,
                9,
                Severity.FATAL,
                Implementation(
                        LogDetector::class.java,
                        Scope.JAVA_FILE_SCOPE
                )
        )

        private const val LOG_CLS = "android.util.Log"
        private const val SYSTEM_CLS = "java.io.PrintStream"
        private const val PRINTLN = "println"
        private const val PRINT = "print"
    }
}
