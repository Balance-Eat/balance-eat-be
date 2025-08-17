package org.balanceeat.domain.config

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

object FixtureReflectionUtils {
    
    /**
     * Reflects fixture field values to the target object using Kotlin reflection
     */
    fun <T : Any> reflect(target: T, fixture: Any): T {
        val targetClass = target::class
        val fixtureClass = fixture::class
        
        // Get all mutable properties from target object
        val targetProperties = targetClass.memberProperties
            .filterIsInstance<KMutableProperty1<T, Any?>>()
        
        // Get all properties from fixture object
        val fixtureProperties = fixtureClass.memberProperties
        
        // Set values from fixture to target
        for (targetProperty in targetProperties) {
            val fixtureProperty = fixtureProperties.find { it.name == targetProperty.name }
            
            if (fixtureProperty != null) {
                try {
                    // Make properties accessible
                    targetProperty.isAccessible = true
                    fixtureProperty.isAccessible = true
                    
                    // Get value from fixture and set to target
                    val value = fixtureProperty.get(fixture)
                    if (value != null) {
                        targetProperty.set(target, value)
                    }
                } catch (e: Exception) {
                    // Skip properties that can't be set
                }
            }
        }
        
        return target
    }
    
    /**
     * Creates a new instance of the specified class and reflects fixture values to it
     */
    inline fun <reified T : Any> createAndReflect(fixture: Any): T {
        val targetClass = T::class
        val instance = createInstance(targetClass)
        return reflect(instance, fixture)
    }
    
    /**
     * Creates an instance of the specified class using its primary constructor with default values
     */
    fun <T : Any> createInstance(clazz: KClass<T>): T {
        val constructor = clazz.primaryConstructor
            ?: throw IllegalArgumentException("Class ${clazz.simpleName} must have a primary constructor")
        
        // Try to create instance with default parameters
        return try {
            constructor.callBy(emptyMap())
        } catch (e: Exception) {
            throw RuntimeException("Failed to create instance of ${clazz.simpleName}. Ensure all constructor parameters have default values.", e)
        }
    }
    
    /**
     * Sets a specific field value on the target object
     */
    fun <T : Any> setField(target: T, fieldName: String, value: Any?) {
        val targetClass = target::class
        val property = targetClass.memberProperties
            .filterIsInstance<KMutableProperty1<T, Any?>>()
            .find { it.name == fieldName }
            ?: throw IllegalArgumentException("Field '$fieldName' not found in ${targetClass.simpleName}")
        
        property.isAccessible = true
        property.set(target, value)
    }
    
    /**
     * Gets a specific field value from the target object
     */
    fun <T : Any> getField(target: T, fieldName: String): Any? {
        val targetClass = target::class
        val property = targetClass.memberProperties
            .find { it.name == fieldName }
            ?: throw IllegalArgumentException("Field '$fieldName' not found in ${targetClass.simpleName}")
        
        property.isAccessible = true
        return property.get(target)
    }
}