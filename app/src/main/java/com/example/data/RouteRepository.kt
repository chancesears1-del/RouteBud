package com.example.data

import kotlinx.coroutines.flow.Flow

class RouteRepository(private val routeDao: RouteDao) {
    val allRoutes: Flow<List<SavedRoute>> = routeDao.getAllRoutes()

    fun getRoutesForUser(username: String): Flow<List<SavedRoute>> = routeDao.getRoutesForUser(username)

    suspend fun insertRoute(route: SavedRoute): Long = routeDao.insertRoute(route)

    suspend fun deleteRoute(id: Int) = routeDao.deleteRoute(id)

    suspend fun clearAll() = routeDao.clearAll()
}
