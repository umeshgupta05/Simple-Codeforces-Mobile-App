package com.example.codeforces.data.repository

import com.example.codeforces.data.api.BackendApiService
import com.example.codeforces.data.model.ExecutionRequest
import com.example.codeforces.data.model.ExecutionResult
import com.example.codeforces.data.model.TestCase
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ExecutionRepository {
    suspend fun runCode(request: ExecutionRequest): Resource<ExecutionResult>
    suspend fun fetchTests(problemId: String): Resource<List<TestCase>>
    suspend fun addCommunityTest(problemId: String, test: TestCase): Resource<TestCase>
}

class ExecutionRepositoryImpl(
    private val api: BackendApiService
) : ExecutionRepository {

    override suspend fun runCode(request: ExecutionRequest): Resource<ExecutionResult> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Resource.Success(api.execute(request))
            } catch (t: Throwable) {
                Resource.Error(message = t.message, throwable = t)
            }
        }

    override suspend fun fetchTests(problemId: String): Resource<List<TestCase>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Resource.Success(api.getTests(problemId))
            } catch (t: Throwable) {
                Resource.Error(message = t.message, throwable = t)
            }
        }

    override suspend fun addCommunityTest(
        problemId: String,
        test: TestCase
    ): Resource<TestCase> = withContext(Dispatchers.IO) {
        return@withContext try {
            Resource.Success(api.addTest(problemId, test))
        } catch (t: Throwable) {
            Resource.Error(message = t.message, throwable = t)
        }
    }
}

