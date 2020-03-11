package org.skyfox.api.mysqlsimplify

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class Database {


    companion object {
        val instance = Database()
        var connection: Connection? = null

        init {
            try {
                Class.forName("com.mysql.jdbc.Driver")
            } catch (e: ClassNotFoundException) {
                System.err.println("[MySQLSimplify] Error while loading the JDBC-Driver")
                System.err.println(e.message)
                e.printStackTrace()
            }
        }
    }



    //<---------------------------------------/*CONNECTION_SERVICE*/------------------------------------------------->//
    //region Connection

    fun connectToServer(url: String, port: Int, username: String, password: String) {
        try {
            if(connection != null) {
                return
            }
            println("[MySQLSimplify] Start connecting to Database...")

            connection = DriverManager.getConnection("jdbc:mysql://$url:$port", username, password)

            if(!connection!!.isClosed) {
                val metadata = connection!!.metaData
                println("[MySQLSimplify] Connection established: ${metadata.url} with user ${metadata.userName}")
            }
        } catch (e: SQLException) {
            System.err.println("[MySQLSimplify] Problem while connecting to database at $url:$port with user $username")
            throw e
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                try {
                    if(!connection!!.isClosed && connection != null) {
                        connection!!.close()
                        if(connection!!.isClosed) {
                            connection = null
                            println("[MySQLSimplify] Connection to Database has been closed!")
                        }
                    }
                } catch (e: SQLException) {
                    System.err.println("[MySQLSimplify] Error while closing Database-Connection!")
                    System.err.println(e.message)
                    e.printStackTrace()
                }
            }
        })
    }

    fun loadDatabase(databaseName: String) {
        var statement: Statement? = null

        try {
            statement = connection?.createStatement()
            statement?.execute("USE $databaseName;")
        } catch (e: SQLException) {
            System.err.println("[MySQLSimplify] Problems by using database $databaseName")
            System.err.println(e.message)
        } finally {
            statement?.close()
        }
    }

    //endregion


    //<-----------------------------------------/*TABLE_SERVICE*/---------------------------------------------------->//


}