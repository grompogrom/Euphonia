package com.euphoiniateam.euphonia.ui.settings

import com.euphoiniateam.euphonia.domain.models.VKUser
import com.vk.api.sdk.VKApiJSONResponseParser
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class VKUsersCommand(private val uids: IntArray = intArrayOf()) : ApiCommand<List<VKUser>>() {
    override fun onExecute(manager: VKApiManager): List<VKUser> {

        if (uids.isEmpty()) {
            // if no uids, send user's data
            val call = VKMethodCall.Builder()
                .method("users.get")
                .args("fields", "photo_200")
                .version(manager.config.version)
                .build()
            return manager.execute(call, ResponseApiParser())
        } else {
            val result = ArrayList<VKUser>()
            val chunks = uids.toList().chunked(CHUNK_LIMIT)
            for (chunk in chunks) {
                val call = VKMethodCall.Builder()
                    .method("users.get")
                    .args("user_ids", chunk.joinToString(","))
                    .args("fields", "photo_200")
                    .version(manager.config.version)
                    .build()
                result.addAll(manager.execute(call, ResponseApiParser()))
            }
            return result
        }
    }

    companion object {
        const val CHUNK_LIMIT = 900
    }

    private class ResponseApiParser : VKApiJSONResponseParser<List<VKUser>> {
        override fun parse(responseJson: JSONObject): List<VKUser> {
            try {
                val ja = responseJson.getJSONArray("response")
                val r = ArrayList<VKUser>(ja.length())
                for (i in 0 until ja.length()) {
                    val user = VKUser.parse(ja.getJSONObject(i))
                    r.add(user)
                }
                return r
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
