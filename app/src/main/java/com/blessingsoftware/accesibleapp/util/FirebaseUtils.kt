package com.blessingsoftware.accesibleapp.util

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

//Funcion para gestionar los callbacks al conectar con firebase, usamos async/await para hacer la consulta y esperar el resltado y asi poder implementarlo con MVVM
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            if (it.exception != null) {
                cont.resumeWithException(it.exception!!)
            } else {
                cont.resume(it.result, null)
            }
        }
    }
}