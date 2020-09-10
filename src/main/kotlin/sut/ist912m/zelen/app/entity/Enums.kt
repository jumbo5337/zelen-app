package sut.ist912m.zelen.app.entity

enum class Role(val id: Int, val value: String) {
    USER(1, "USER"),
    ADMIN(2, "ADMIN"),
    BLOCKED(-1, "BLOCKED");

    companion object {
        @JvmStatic
        fun byId(id: Int): Role {
            return values().find { it.id == id }
                    ?: error("Unable to find [${this::class.java.simpleName}] with id [$id]")
        }
    }

}

enum class OpType(val id: Int) {
    DEPOSIT(1),
    TRANSFER(2),
    WITHDRAWAL(3);

    companion object {
        @JvmStatic
        fun byId(id: Int): OpType {
            return values().find { it.id == id }
                    ?: error("Unable to find [${this::class.java.simpleName}] with id [$id]")
        }
    }
}

enum class OpState(val id: Int) {
    CREATED(0),
    COMPLETED(1),
    CANCELED(-1);

    companion object {
        @JvmStatic
        fun byId(id: Int): OpState {
            return values().find { it.id == id }
                    ?: error("Unable to find [${this::class.java.simpleName}] with id [$id]")
        }
    }
}

enum class Privacy(val id: Int) {
    SHOW_ALL(1),
    SHOW_NAME(2),
    HIDDEN(3);

    companion object {
        @JvmStatic
        fun byId(id: Int): Privacy {
            return values().find { it.id == id }
                    ?: error("Unable to find [${this::class.java.simpleName}] with id [$id]")
        }
    }
}


enum class AdminOpType(val id: Int) {
    BAN(1),
    UNBAN(2),
    CANCELLATION(3);

    companion object {
        @JvmStatic
        fun byId(id: Int): AdminOpType {
            return values().find { it.id == id }
                    ?: error("Unable to find [${this::class.java.simpleName}] with id [$id]")
        }
    }
}
