package com.example.codeforces.data.repository

import com.example.codeforces.data.api.CodeforcesApiService
import com.example.codeforces.data.model.Problem
import com.example.codeforces.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProblemRepository {
    suspend fun loadProblemset(tags: List<String> = emptyList()): Resource<List<Problem>>
    suspend fun findProblem(problemId: String): Problem?
    suspend fun cacheProblem(problem: Problem)
}

class ProblemRepositoryImpl(
    private val api: CodeforcesApiService
) : ProblemRepository {

    private val memoryCache = LinkedHashMap<String, Problem>()

    override suspend fun loadProblemset(tags: List<String>): Resource<List<Problem>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val tagQuery = tags.takeIf { it.isNotEmpty() }?.joinToString(";")
                val response = api.getProblemset(tagQuery)
                val problems = response.result.problems.map { dto ->
                    val stats = response.result.statistics.firstOrNull {
                        it.contestId == dto.contestId && it.index == dto.index
                    }
                    val id = buildProblemId(dto.contestId, dto.index)
                    Problem(
                        id = id,
                        contestId = dto.contestId,
                        index = dto.index,
                        name = dto.name,
                        rating = dto.rating,
                        tags = dto.tags,
                        solvedCount = stats?.solvedCount
                    ).also { memoryCache[id] = it }
                }
                Resource.Success(problems)
            } catch (t: Throwable) {
                Resource.Error(message = t.message, throwable = t)
            }
        }

    override suspend fun findProblem(problemId: String): Problem? =
        withContext(Dispatchers.IO) { memoryCache[problemId] }

    override suspend fun cacheProblem(problem: Problem) {
        memoryCache[problem.id] = problem
    }

    private fun buildProblemId(contestId: Int?, index: String): String =
        if (contestId != null) "$contestId-$index" else index
}

