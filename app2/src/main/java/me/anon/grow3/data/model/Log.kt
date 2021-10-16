package me.anon.grow3.data.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import me.anon.grow3.data.exceptions.GrowTrackerException
import me.anon.grow3.ui.action.view.LogView
import me.anon.grow3.util.*
import me.anon.grow3.view.model.Card
import org.threeten.bp.ZonedDateTime
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "action")
@JsonSubTypes(
	JsonSubTypes.Type(value = Environment::class, name = "Environment"),
	JsonSubTypes.Type(value = Transplant::class, name = "Transplant"),
	JsonSubTypes.Type(value = Harvest::class, name = "Harvest"),
	JsonSubTypes.Type(value = Maintenance::class, name = "Maintenance"),
	JsonSubTypes.Type(value = Pesticide::class, name = "Pesticide"),
	JsonSubTypes.Type(value = Photo::class, name = "Photo"),
	JsonSubTypes.Type(value = StageChange::class, name = "StageChange"),
	JsonSubTypes.Type(value = Water::class, name = "Water")
)
abstract class Log(
	open val id: String = UUID.randomUUID().toString(),
	open var date: String = ZonedDateTime.now().asApiString(),
	open var notes: String = "",
	open var cropIds: List<String> = arrayListOf(),
	open var action: String = "Log"
)
{
	public var isDraft = false

	open fun summary(): CharSequence = ""
	open val typeRes: Int = -1
}

public fun Log.asView(diary: Diary): LogView<*>
{
	return when (this)
	{
		is Water -> logView(diary)
		is StageChange -> logView(diary)
		is Photo -> logView(diary)
		else -> throw GrowTrackerException.InvalidLog(this)
	}
}

public fun Log.asCard(diary: Diary): Card<*>
{
	return when (this)
	{
		is Water -> logCard(diary)
		is StageChange -> logCard(diary)
		is Photo -> logCard(diary)
		else -> throw GrowTrackerException.InvalidLog(this)
	}
}

data class LogChange(
	var days: Int
) : Delta()

public fun Duo<Log>.difference(): LogChange = LogChange((first.date and second!!.date).dateDifferenceDays())