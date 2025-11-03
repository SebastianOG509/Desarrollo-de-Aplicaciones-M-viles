package com.example.reto8.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CompanyDao(context: Context) :
    SQLiteOpenHelper(context, "companies.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE company (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                classification TEXT NOT NULL,
                url TEXT,
                phone TEXT,
                email TEXT,
                products TEXT
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS company")
        onCreate(db)
    }

    // 🔹 Insertar o actualizar empresa
    fun insertOrUpdate(company: Company) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", company.name)
            put("classification", company.classification)
            put("url", company.url)
            put("phone", company.phone)
            put("email", company.email)
            put("products", company.products)
        }

        if (company.id == 0L) {
            db.insert("company", null, values)
        } else {
            db.update(
                "company",
                values,
                "id = ?",
                arrayOf(company.id.toString())
            )
        }

        db.close()
    }

    // 🔹 Obtener todas las empresas
    fun getAll(): List<Company> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM company ORDER BY id DESC", null)
        val list = mutableListOf<Company>()
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Company(
                        id = cursor.getLong(0),
                        name = cursor.getString(1),
                        classification = cursor.getString(2),
                        url = cursor.getString(3),
                        phone = cursor.getString(4),
                        email = cursor.getString(5),
                        products = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // 🔹 Filtrar por texto y clasificación
    fun filter(search: String, classification: String): List<Company> {
        val db = readableDatabase
        val whereClauses = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (search.isNotEmpty()) {
            whereClauses.add("name LIKE ?")
            args.add("%$search%")
        }
        if (classification != "Todos") {
            whereClauses.add("classification = ?")
            args.add(classification)
        }

        val where = if (whereClauses.isNotEmpty())
            "WHERE ${whereClauses.joinToString(" AND ")}" else ""

        val cursor = db.rawQuery(
            "SELECT * FROM company $where ORDER BY id DESC",
            args.toTypedArray()
        )

        val list = mutableListOf<Company>()
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Company(
                        id = cursor.getLong(0),
                        name = cursor.getString(1),
                        classification = cursor.getString(2),
                        url = cursor.getString(3),
                        phone = cursor.getString(4),
                        email = cursor.getString(5),
                        products = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // 🔹 Eliminar empresa
    fun delete(id: Long) {
        val db = writableDatabase
        db.delete("company", "id = ?", arrayOf(id.toString()))
        db.close()
    }
}
