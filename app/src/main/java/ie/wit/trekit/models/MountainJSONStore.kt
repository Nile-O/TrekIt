package ie.wit.trekit.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import ie.wit.trekit.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "mountains.json"
val gsonBuilder = GsonBuilder().registerTypeAdapter(Uri::class.java, UriParser()).create()
val listType: Type = object : TypeToken<ArrayList<MountainModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class MountainJSONStore(private val context: Context) : MountainStore {

    var mountains = mutableListOf<MountainModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<MountainModel> {
        logAll()
        return mountains
    }

    override fun create(mountain: MountainModel) {
        mountains.add(mountain)

    }


    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        mountains = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        mountains.forEach { Timber.i("$it") }
    }
}
class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}
