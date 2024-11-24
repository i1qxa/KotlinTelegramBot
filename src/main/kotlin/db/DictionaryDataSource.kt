package org.example.db

import org.example.LearnWordsTrainer
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

fun main() {
    val connection: Connection? = null
//    try {
//        connection = DriverManager.getConnection("jdbc:sqlite:sample.db")
//        val statement: Statement = connection.createStatement()
//        statement.queryTimeout = 30 // set timeout to 30 sec.
//        statement.executeUpdate("drop table if exists person")
//        statement.executeUpdate("create table person (id integer, name string)")
//        statement.executeUpdate("insert into person values(1, 'leo')")
//        statement.executeUpdate("insert into person values(2, 'yui')")
//        val rs: ResultSet = statement.executeQuery("select * from person")
//        while (rs.next()) {
//            // read the result set
//            println("name = " + rs.getString("name"))
//            println("id = " + rs.getInt("id"))
//        }
//    } catch (e: SQLException) {
//        // if the error message is 'out of memory',
//        // it probably means no database file is found
//        System.err.println(e.message)
//    } finally {
//        try {
//            connection?.close()
//        } catch (e: SQLException) {
//            // connection close failed.
//            System.err.println(e.message)
//        }
//    }

    try {
        DriverManager.getConnection("jdbc:sqlite:data.db")
            .use { connection ->
                val statement = connection.createStatement()
                statement.executeUpdate(
                    """
                      
                    CREATE TABLE IF NOT EXISTS 'users' (
                      'id' integer PRIMARY KEY,
                      'username' varchar,
                      'created_at' timestamp,
                      'chat_id' integer
                    );
                    
                    CREATE TABLE IF NOT EXISTS 'words' (
                      'id' integer PRIMARY KEY,
                      'text' varchar,
                      'translate' varchar
                    );
                    
                    CREATE TABLE IF NOT EXISTS 'user_answers' (
                      'user_id' integer,  
                      'word_id' integer,
                      'correct_answer_count' integer,
                      'updated_at' timestamp,
                      FOREIGN KEY(user_id) REFERENCES users(id),
                      FOREIGN KEY(word_id) REFERENCES words(id)
                      
                    );
                    

            """.trimIndent()
                )
                updateDictionary(statement)
            }
    } catch (e: SQLException) {
        // if the error message is 'out of memory',
        // it probably means no database file is found
        System.err.println(e.message)
    } finally {
        try {
            connection?.close()
        } catch (e: SQLException) {
            // connection close failed.
            System.err.println(e.message)
        }
    }


}

fun updateDictionary(statement: Statement) {
    var index = 1
    LearnWordsTrainer().dictionary.forEach {
        statement.executeUpdate("insert into words values(${index++}, '${it.original}', '${it.translated}')")
    }
}