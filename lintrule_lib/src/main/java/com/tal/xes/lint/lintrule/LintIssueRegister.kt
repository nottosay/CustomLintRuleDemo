package com.tal.xes.lint.lintrule

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.tal.xes.lint.lintrule.detectors.ActivityLayoutNameDetector
import com.tal.xes.lint.lintrule.detectors.IndexOutOfBoundsDetector
import com.tal.xes.lint.lintrule.detectors.LogDetector
import com.tal.xes.lint.lintrule.detectors.NumberFormatDetector
import java.util.*

class LintIssueRegister : IssueRegistry() {
    override fun getIssues(): List<Issue> {
        return Arrays.asList(
                ActivityLayoutNameDetector.ISSUE,
                LogDetector.ISSUE,
                IndexOutOfBoundsDetector.ISSUE,
                NumberFormatDetector.ISSUE
        )
    }
}
