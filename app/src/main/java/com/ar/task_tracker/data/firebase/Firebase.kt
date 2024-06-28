package com.ar.task_tracker.data.firebase

import android.net.Uri
import com.ar.task_tracker.domain.model.FireBaseResponse
import com.ar.task_tracker.domain.model.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class Firebase {
    companion object {
        suspend fun getTaskDetailsFromFireBase(): List<FireBaseResponse> =
            withContext(Dispatchers.IO) {
                val taskList = mutableListOf<FireBaseResponse>()
                val fb = FirebaseFirestore.getInstance()
                val tasksSnapshot = fb.collection("tasks").get().await()
                for (item in tasksSnapshot) {
                    taskList.add(
                        FireBaseResponse(
                            id = item.id.toInt(),
                            description = item.get("description").toString(),
                            image = if (item.get("image") == null) {
                                null
                            } else {
                                item.get("image").toString()
                            }
                        )
                    )
                }
                return@withContext taskList
            }

        suspend fun saveTaskDetailsInFireBase(task: Task): Boolean = withContext(Dispatchers.IO) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()

            return@withContext try {
                if (task.image != null) {
                    val storageChild = storage.reference.child("image/${task.id}")
                    println("Image -> ${task.image}")
                    val uploadTask = storageChild.putFile(Uri.parse(task.image)).await()
                    val uri = storageChild.downloadUrl.await()
                    firestore.collection("tasks").document(task.id.toString())
                        .set(
                            hashMapOf(
                                "description" to task.description,
                                "image" to uri.toString()
                            )
                        ).await()
                    true
                } else {
                    firestore.collection("tasks").document(task.id.toString())
                        .set(
                            hashMapOf(
                                "description" to task.description,
                                "image" to null
                            )
                        ).await()
                    true
                }
            } catch (e: Exception) {
                false
            }
        }

        suspend fun deleteTask(taskId: Int): Boolean = withContext(Dispatchers.IO){
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            firestore.collection("tasks").document(taskId.toString()).delete().await()
            storage.reference.child("image/${taskId}").delete().await()
            return@withContext true
        }
    }
}