package io.alexaldev.sportsevents.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.alexaldev.sportsevents.R
import io.alexaldev.sportsevents.databinding.CellSportEventBinding

class EventsAdapter(
    private val eventsList: MutableList<EventViewItem> = mutableListOf(),
    private val onFavoriteEventListener: (EventViewItem) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EventViewHolder(CellSportEventBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {
        holder.bind(eventsList[position])
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    inner class EventViewHolder(private val viewBinding: CellSportEventBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(eventViewItem: EventViewItem) {
            with(viewBinding) {
                countdown.text = eventViewItem.remaining
                competitor1.text = eventViewItem.firstCompetitor
                competitor2.text = eventViewItem.secondCompetitor
                val favoriteIcon =
                    if (eventViewItem.isFavorite) R.drawable.ic_star_fill else R.drawable.ic_star
                favorite.setImageResource(favoriteIcon)
                favorite.setOnClickListener { onFavoriteEventListener(eventViewItem) }
            }
        }
    }
}