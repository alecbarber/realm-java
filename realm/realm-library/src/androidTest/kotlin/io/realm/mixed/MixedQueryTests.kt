/*
 * Copyright 2021 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.mixed

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.realm.*
import io.realm.entities.MixedNotIndexed
import io.realm.kotlin.where
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.collections.HashSet
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MixedQueryTests {
    private lateinit var realmConfiguration: RealmConfiguration
    private lateinit var realm: Realm

    private fun initializeTestData() {
        val mixedValues = MixedHelper.generateMixedValues()

        realm.beginTransaction()

        for (value in mixedValues) {
            val mixedObject = MixedNotIndexed(value)
            realm.insert(mixedObject)
        }

        realm.commitTransaction()
    }

    @get:Rule
    val configFactory = TestRealmConfigurationFactory()

    init {
        Realm.init(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun setUp() {
        realmConfiguration = configFactory.createSchemaConfiguration(
                false,
                MixedNotIndexed::class.java)

        realm = Realm.getInstance(realmConfiguration)

        initializeTestData()
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun isNull() {
        val results = realm.where<MixedNotIndexed>().isNull(MixedNotIndexed.FIELD_MIXED).findAll()
        assertEquals(9, results.size)
        for (result in results) {
            assertTrue(result.mixed!!.isNull)
        }
    }

    @Test
    fun isNotNull() {
        val results = realm.where<MixedNotIndexed>().isNotNull(MixedNotIndexed.FIELD_MIXED).findAll()
        assertEquals(97, results.size)
        for (result in results) {
            assertFalse(result.mixed!!.isNull)
        }
    }

    @Test
    fun isEmpty() {
        val query: RealmQuery<MixedNotIndexed> = realm.where()
        assertFailsWith<IllegalArgumentException> {
            query.isEmpty(MixedNotIndexed.FIELD_MIXED)
        }
    }

    @Test
    fun isNotEmpty() {
        val query: RealmQuery<MixedNotIndexed> = realm.where()
        assertFailsWith<IllegalArgumentException> {
            query.isEmpty(MixedNotIndexed.FIELD_MIXED)
        }
    }

    @Test
    fun count() {
        val value = realm.where<MixedNotIndexed>().count()
        assertEquals(106, value)
    }

    @Test
    fun sort() {
        val results = realm.where<MixedNotIndexed>().sort(MixedNotIndexed.FIELD_MIXED).findAll()
        assertEquals(106, results.size)
        assertTrue(results.first()!!.mixed!!.isNull)
        assertEquals(MixedType.UUID, results.last()!!.mixed!!.type)
    }

    @Test
    fun distinct() {
        val results = realm.where<MixedNotIndexed>().distinct(MixedNotIndexed.FIELD_MIXED).findAll()

        val hashSet = HashSet<Mixed>()
        for (result in results){
            hashSet.add(result.mixed!!)
        }
        assertEquals(60, results.size)
        assertEquals(hashSet.size, results.size)
    }
}