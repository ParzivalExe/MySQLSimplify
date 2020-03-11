package org.skyfox.api.mysqlsimplify

enum class Selector(val selectorString: String) {

    EQUALS("="),
    NOT_EQUALS("!="),
    SMALLER("<"),
    BIGGER(">")


}