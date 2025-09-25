package com.kmcounty.app.data.logging

import android.content.Context
import androidx.room.*
import com.kmcounty.core.model.ParsedRide
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Entidade para armazenar logs de detecção de corridas (opt-in)
 */
@Entity(tableName = "ride_logs")
data class RideLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "distance")
    val distance: Double,

    @ColumnInfo(name = "time")
    val time: Int,

    @ColumnInfo(name = "price_per_km")
    val pricePerKm: Double,

    @ColumnInfo(name = "price_per_minute")
    val pricePerMinute: Double,

    @ColumnInfo(name = "confidence")
    val confidence: Float,

    @ColumnInfo(name = "source")
    val source: String, // "accessibility" ou "ocr"

    @ColumnInfo(name = "recommended_color")
    val recommendedColor: String, // "GREEN", "ORANGE", "RED"

    @ColumnInfo(name = "app_package")
    val appPackage: String? = null, // Pacote do app onde foi detectado

    @ColumnInfo(name = "raw_text")
    val rawText: String? = null // Texto bruto detectado (para debug)
) {
    companion object {
        fun fromParsedRide(
            ride: ParsedRide,
            appPackage: String? = null,
            rawText: String? = null
        ): RideLog {
            return RideLog(
                timestamp = System.currentTimeMillis(),
                price = ride.price,
                distance = ride.distance,
                time = ride.time,
                pricePerKm = ride.pricePerKm,
                pricePerMinute = ride.pricePerMinute,
                confidence = ride.confidence,
                source = ride.source,
                recommendedColor = ride.getRecommendedColor().name,
                appPackage = appPackage,
                rawText = rawText
            )
        }
    }
}

/**
 * DAO para operações com logs de corrida
 */
@Dao
interface RideLogDao {

    @Query("SELECT * FROM ride_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<RideLog>>

    @Query("SELECT * FROM ride_logs WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getLogsSince(startTime: Long): Flow<List<RideLog>>

    @Query("SELECT * FROM ride_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<RideLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RideLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<RideLog>)

    @Query("DELETE FROM ride_logs WHERE id = :logId")
    suspend fun deleteLog(logId: Long)

    @Query("DELETE FROM ride_logs WHERE timestamp < :beforeTime")
    suspend fun deleteLogsBefore(beforeTime: Long)

    @Query("DELETE FROM ride_logs")
    suspend fun deleteAllLogs()

    @Query("SELECT COUNT(*) FROM ride_logs")
    fun getLogCount(): Flow<Int>

    @Query("SELECT AVG(price_per_km) FROM ride_logs WHERE timestamp >= :startTime")
    fun getAveragePricePerKm(startTime: Long): Flow<Double?>

    @Query("SELECT SUM(price) FROM ride_logs WHERE timestamp >= :startTime")
    fun getTotalEarnings(startTime: Long): Flow<Double?>
}

/**
 * Database Room para logs
 */
@Database(
    entities = [RideLog::class],
    version = 1,
    exportSchema = true
)
abstract class RideLogDatabase : RoomDatabase() {
    abstract fun rideLogDao(): RideLogDao

    companion object {
        @Volatile
        private var INSTANCE: RideLogDatabase? = null

        fun getDatabase(context: Context): RideLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RideLogDatabase::class.java,
                    "ride_logs.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Repository para gerenciar logs de corrida
 */
class RideLogRepository(private val context: Context) {

    private val database = RideLogDatabase.getDatabase(context)
    private val dao = database.rideLogDao()

    val allLogs = dao.getAllLogs()
    val logCount = dao.getLogCount()

    fun getLogsSince(startTime: Long) = dao.getLogsSince(startTime)
    fun getRecentLogs(limit: Int) = dao.getRecentLogs(limit)
    fun getAveragePricePerKm(startTime: Long) = dao.getAveragePricePerKm(startTime)
    fun getTotalEarnings(startTime: Long) = dao.getTotalEarnings(startTime)

    suspend fun insertLog(ride: ParsedRide, appPackage: String? = null, rawText: String? = null) {
        val log = RideLog.fromParsedRide(ride, appPackage, rawText)
        dao.insertLog(log)
    }

    suspend fun deleteLog(logId: Long) {
        dao.deleteLog(logId)
    }

    suspend fun deleteOldLogs(olderThanDays: Int = 30) {
        val cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
        dao.deleteLogsBefore(cutoffTime)
    }

    suspend fun deleteAllLogs() {
        dao.deleteAllLogs()
    }

    suspend fun exportLogs(): List<RideLog> {
        return dao.getAllLogs().first()
    }
}
