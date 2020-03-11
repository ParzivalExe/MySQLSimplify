package org.skyfox.api.mysqlsimplify

import java.sql.SQLException
import java.sql.Statement

class Table(var tableName: String, var tableValues: Array<TableValue>, var override: Boolean) {

    private var _createStatement: String? = null
    var createStatement: String
        get() {
            return if(_createStatement == null) {
                createCreateString()
            }else{
                _createStatement!!
            }
        }
        set(value) {_createStatement = value}


    fun createTable(): Table {
        var statement: Statement? = null
        try {
            statement = Database.connection?.createStatement()
            statement?.execute(createStatement)
        } catch (e: SQLException) {
            System.err.println("[MySQLSimplify] Problem with creating Table $tableName")
            System.err.println("[MySQLSimplify] CreateStatement: $createStatement")
            System.err.println(e.message)
            throw e
        } finally {
            statement?.close()
        }
        return this
    }
    private fun createCreateString(): String {
        //CREATE HEADER
        var createString = "CREATE TABLE "
        if(!override) createString += "IF NOT EXISTS "
        createString += "$tableName ("

        //SET VALUES
        var primaryKeyString = "PRIMARY KEY ("
        tableValues.forEach { tableValue ->
            createString += tableValue.createStatement + ", "
            if(tableValue.isLinked) {
                createString += tableValue.link!!.createInitString() + ", "
            }
            if(tableValue.primaryKey) {
                primaryKeyString += tableValue.valueName + ", "
            }
        }
        //SET PRIMARY KEY(s)
        //remove last ,
        primaryKeyString = primaryKeyString.substring(0, primaryKeyString.length-2)
        primaryKeyString += ")"
        createString += primaryKeyString

        //CREATE TALE
        createString += ");"
        return createString
    }

}