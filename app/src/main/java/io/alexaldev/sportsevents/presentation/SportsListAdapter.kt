package io.alexaldev.sportsevents.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.alexaldev.sportsevents.R
import io.alexaldev.sportsevents.databinding.CellSportGroupBinding

class SportsListAdapter(
    private val context: Context,
    val sportsList: MutableList<SportViewItem>,
    private val onFavoriteEventListener: (EventViewItem) -> Unit,
    private val onToggleFilterListener: (SportViewItem, Boolean) -> Unit,
    private val onExpandCollapseListener: (SportViewItem, Boolean) -> Unit
) : RecyclerView.Adapter<SportsListAdapter.SportViewHolder>() {

    fun updateData(newItems: List<SportViewItem>) {
        this.sportsList.clear()
        this.sportsList.addAll(newItems)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SportViewHolder {
        return SportViewHolder(
            CellSportGroupBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SportViewHolder, position: Int, payloads: List<Any?>) {
        if (payloads.contains("TIMER_ONLY")) {
            holder.bindOnlyTimer(position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(
        holder: SportViewHolder,
        position: Int
    ) {
        holder.bindAll(sportsList[position])
    }

    override fun getItemCount(): Int {
        return sportsList.size
    }

    inner class SportViewHolder(private val viewBinding: CellSportGroupBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bindOnlyTimer(position: Int) {
            val eventsAdapter = (viewBinding.eventsList.adapter as? EventsAdapter) ?: return
            eventsAdapter.notifyItemChanged(position)
        }

        fun bindAll(sportViewItem: SportViewItem) {
            with(viewBinding) {

                sportTitle.text = sportViewItem.title
                // Need to reset first to not get
                // triggered when binding the same view item again
                favoriteToggle.setOnCheckedChangeListener(null)

                favoriteToggle.isChecked = sportViewItem.isFilterEnabled
                favoriteToggle.setOnCheckedChangeListener { b, checked ->
                    onToggleFilterListener(sportViewItem, checked)
                }
                val collapseIcon =
                    if (sportViewItem.isExpanded) R.drawable.ic_arrow_collapse else R.drawable.ic_arrow_expand
                sportExpand.setImageResource(collapseIcon)
                sportExpand.setOnClickListener {
                    onExpandCollapseListener(sportViewItem, !sportViewItem.isExpanded)
                }
                if (sportViewItem.isExpanded) {
                    val eventsToShow =
                        if (sportViewItem.isFilterEnabled) sportViewItem.events.filter { it.isFavorite } else sportViewItem.events
                    val eventsAdapter =
                        EventsAdapter(eventsToShow.toMutableList(), onFavoriteEventListener)
                    val layoutManager = GridLayoutManager(context, 4)
                    eventsList.visibility = View.VISIBLE
                    eventsList.layoutManager = layoutManager
                    eventsList.adapter = eventsAdapter
                } else {
                    eventsList.visibility = View.GONE
                }
            }
        }
    }
}