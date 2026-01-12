package com.example.codeforces.data.repository

import com.example.codeforces.data.model.ExecutionRequest
import com.example.codeforces.data.model.ExecutionResult
import com.example.codeforces.data.model.ExecutionStatus
import com.example.codeforces.data.model.TestCase
import com.example.codeforces.utils.Resource

/**
 * Explicit no-backend placeholder. Returns errors to indicate execution is unavailable.
 * This is not a fake success; it simply short-circuits until a backend is provided.
 */
class NoBackendExecutionRepository : ExecutionRepository {
    override suspend fun runCode(request: ExecutionRequest): Resource<ExecutionResult> =
        Resource.Error("Execution unavailable: no backend configured.")

    override suspend fun fetchTests(problemId: String): Resource<List<TestCase>> =
        Resource.Error("Community tests unavailable: no backend configured.")

    override suspend fun addCommunityTest(
        problemId: String,
        test: TestCase
    ): Resource<TestCase> = Resource.Error("Cannot add test: no backend configured.")
}

