package org.skyfox.api.mysqlsimplify

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class Query(var query: String, var values: ArrayList<Any?>) {

    var resultKeys: ArrayList<String> = arrayListOf()


    fun executeQuery(): Results {
        var preparedStatement: PreparedStatement? = null
        var results = Results()
        try {
            preparedStatement = Database.connection?.prepareStatement(query)
            if(preparedStatement != null) {
                for((index, value) in values.withIndex()) {
                    preparedStatement.setObject(index+1, value)
                }
                val resultSet = preparedStatement.executeQuery()
                results = getAllValues(resultSet)
            }
        } catch (e: SQLException) {
            System.err.println("[MySQLSimplify] Problems while executing query: $query")
            System.err.println(e.message)
            throw e
        } finally {
            preparedStatement?.close()
        }
        return results
    }
    fun execute() {
        var preparedStatement: PreparedStatement? = null
        try {
            preparedStatement = Database.connection?.prepareStatement(query)
            if(preparedStatement != null) {
                for((index, value) in values.withIndex()) {
                    preparedStatement.setObject(index+1, value)
                }
                preparedStatement.execute()
            }
        } catch (e: SQLException) {
            System.err.println("[MySQLSimplify] Problem while executing statement: $query")
            System.err.println(e.message)
            throw e
        } finally {
            preparedStatement?.close()
        }
    }

    private fun getAllValues(resultSet: ResultSet): Results {
        val results = Results()
        var column = 0
        while(resultSet.next()) {
            /*FOR EVERY COLUMN*/
            try {
                for(key in resultKeys) {
                    var result: Any? = resultSet.getObject(key)
                    if(resultSet.wasNull()) {
                        result = null
                    }
                    results.addResult(column, key, result)
                }
            } finally {
                column++
            }
        }
        return results
    }


    fun where(column: String, selector: Selector, values: Array<Any?>): Query {
        var newQuery = query.substring(0, query.length-1)
        var valueString = ""
        values.forEach { value ->
            valueString += "?, "
            this.values.add(value)
        }
        valueString = valueString.substring(0, valueString.length-2)
        newQuery += " WHERE $column ${selector.selectorString} $valueString;"
        query = newQuery
        return this
    }
    fun where(column: String, selector: Selector, value: Any?): Query {
        return where(column, selector, arrayOf(value))
    }
    fun andWhere(column: String, selector: Selector, values: Array<Any?>): Query {
        var newQuery = query.substring(0, query.length-1)
        var valueString = ""
        values.forEach { value ->
            valueString += "?, "
            this.values.add(value)
        }
        valueString = valueString.substring(0, valueString.length-2)
        newQuery += " AND $column ${selector.selectorString} $valueString;"
        query = newQuery
        return this
    }
    fun andWhere(column: String, selector: Selector, value: Any?): Query {
        return andWhere(column, selector, arrayOf(value))
    }
    fun orWhere(column: String, selector: Selector, values: Array<Any?>): Query {
        var newQuery = query.substring(0, query.length-1)
        var valueString = ""
        values.forEach { value ->
            valueString += "?, "
            this.values.add(value)
        }
        valueString = valueString.substring(0, valueString.length-2)
        newQuery += " OR $column ${selector.selectorString} $valueString;"
        query = newQuery
        return this
    }
    fun orWhere(column: String, selector: Selector, value: Any?): Query {
        return orWhere(column, selector, arrayOf(value))
    }

    companion object {

        fun insertInto(tableName: String, values: Array<Value>): Query {
            var query = "INSERT INTO $tableName"
            var valueKeys = "("
            var valuesString = "VALUES ("
            val valueArray = arrayListOf<Any?>()
            values.forEach { value ->
                valueKeys += "${value.key}, "
                valuesString += "?, "
                valueArray.add(value.value)
            }
            valueKeys = valueKeys.substring(0, valueKeys.length-2) + ")"
            valuesString = valuesString.substring(0, valuesString.length-2) + ");"
            return Query("$query $valueKeys $valuesString", valueArray)
        }

        fun selectFrom(selectValues: ArrayList<String>, tableName: String): Query {
            var valueString = ""
            selectValues.forEach { value -> valueString += "$value, " }
            valueString = valueString.substring(0, valueString.length-2)

            return Query("SELECT $valueString FROM $tableName;", arrayListOf()).apply {
                resultKeys = selectValues
            }
        }

        fun deleteFrom(tableName: String): Query {
            return Query("DELETE FROM $tableName;", arrayListOf())
        }

        fun update(tableName: String, values: Array<Value>): Query {
            var valueString = ""
            val queryValues = arrayListOf<Any?>()
            values.forEach { value ->
                valueString += "${value.key} = ?, "
                queryValues.add(value.value)
            }
            if(valueString.length >= 2) {
                valueString = valueString.substring(0, valueString.length-2)
            }
            return Query("UPDATE $tableName SET $valueString;", queryValues)
        }

    }

}