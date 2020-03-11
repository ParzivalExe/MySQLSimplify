package org.skyfox.api.mysqlsimplify

class TableValue private constructor(var valueName: String, var valueType: String, var primaryKey: Boolean, var additionalArguments: ArrayList<String>) {

    var link: Link? = null
        private set
    val isLinked: Boolean
        get() = this.link != null

    private var _createStatement: String? = null
    var createStatement: String
        get() {
            return if(_createStatement == null) {
                createInitString()
            }else{
                _createStatement!!
            }
        }
        set(value) {_createStatement = value}

    private fun createInitString(): String {
        var string = "$valueName $valueType"
        additionalArguments.forEach { argument -> string += " $argument" }
        return string
    }

    fun linkValue(linkTable: Table, linkValue: TableValue, updateType: String, deleteType: String) {
        link = Link(this, linkTable, linkValue, updateType, deleteType)
    }
    fun linkValue(linkTable: String, linkValue: String, updateType: String, deleteType: String) {
        link = Link(valueName, linkTable, linkValue, updateType, deleteType)
    }



    companion object {

        fun varchar(valueName: String, length: Int, primaryKey: Boolean, additionalArguments: ArrayList<String>): TableValue {
            return TableValue(valueName, "VARCHAR($length)", primaryKey, additionalArguments)
        }
        fun varchar(valueName: String, length: Int, notNull: Boolean, primaryKey: Boolean): TableValue {
            val additionalArguments = arrayListOf<String>()
            if(notNull) additionalArguments.add("NOT NULL")
            return varchar(valueName, length, primaryKey, additionalArguments)
        }
        fun varchar(valueName: String, length: Int): TableValue {
            return varchar(valueName, length, notNull = false, primaryKey = false)
        }

        fun integer(valueName: String, primaryKey: Boolean, additionalArguments: ArrayList<String>): TableValue {
            return TableValue(valueName, "INTEGER", primaryKey, additionalArguments)
        }
        fun integer(valueName: String, primaryKey: Boolean, notNull: Boolean): TableValue {
            val additionalArguments = arrayListOf<String>()
            if(notNull) additionalArguments.add("NOT NULL")
            return integer(valueName, primaryKey, additionalArguments)
        }
        fun integer(valueName: String): TableValue {
            return integer(valueName, false, false)
        }
    }

    /**
     * @param valueToLink The value you want to link inside this Table
     * @param linkTable The Table you want to establish the link to
     * @param linkValue The value you want to establish the link to
     */
    class Link(var valueToLink: String, var linkTable: String, var linkValue: String, var updateType: String, var deleteType: String) {

        constructor(valueToLink: TableValue, linkTable: Table, linkValue: TableValue, updateType: String, deleteType: String):
                this(valueToLink.valueName, linkTable.tableName, linkValue.valueName, updateType, deleteType)

        fun createInitString(): String {
            return "FOREIGN KEY ($valueToLink) REFERENCES $linkTable($linkValue) " +
                    "ON UPDATE $updateType ON DELETE $deleteType"
        }
    }

}
