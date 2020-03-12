package org.skyfox.api.mysqlsimplify

class Results {

    private val results: ArrayList<HashMap<String, Any?>> = arrayListOf()

    fun addResult(column: Int, key: String, value: Any?) {
        if(results.size-1 >= column) {
            /*COLUMN ALREADY EXISTS*/
            val hashMap = results[column]
            hashMap[key] = value
            results[column] = hashMap
        }else{
            /*NEW COLUMN*/
            val hashMap = hashMapOf<String, Any?>()
            hashMap[key] = value
            results.add(column, hashMap)
        }
    }


    fun getResultsOfColumn(column: Int): HashMap<String, Any?> {
        return results[column]
    }

    fun getResult(column: Int, key: String): Any? {
        return results[column][key]
    }

    fun containsResult(key: String): Boolean {
        return (results[0].containsKey(key))
    }

    fun getResultFromEveryColumn(key: String): Array<Any?> {
        val resultArray = arrayOfNulls<Any?>(results.size)
        for((index, result) in results.withIndex()) {
            resultArray[index] = result
        }
        return resultArray
    }

    fun getAllResults(): ArrayList<HashMap<String, Any?>> {
        return results
    }

    fun getColumnCount(): Int {
        return results.count()
    }

}