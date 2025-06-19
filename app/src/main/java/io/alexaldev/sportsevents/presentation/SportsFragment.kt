package io.alexaldev.sportsevents.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.alexaldev.sportsevents.R
import io.alexaldev.sportsevents.databinding.FragmentAllSportsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SportsFragment : Fragment(R.layout.fragment_all_sports) {

    private val viewBinding by viewBinding(FragmentAllSportsBinding::bind)
    private lateinit var sportsListAdapter: SportsListAdapter
    private lateinit var rvLayoutManager: LinearLayoutManager
    private val viewModel: SportsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rvLayoutManager = LinearLayoutManager(requireContext())
        sportsListAdapter = SportsListAdapter(
            context = requireContext(),
            mutableListOf(),
            onToggleFilterListener = { item: SportViewItem, enabled: Boolean ->
                viewModel.onAction(
                    UserAction.SportFilterToggled(item, enabled)
                )
            },
            onExpandCollapseListener = { item: SportViewItem, expanded: Boolean ->
                val action =
                    if (expanded) UserAction.SportExpanded(item) else UserAction.SportCollapsed(item)
                viewModel.onAction(action)
            },
            onFavoriteEventListener = { viewModel.onAction(UserAction.EventFavorited(it)) }
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel
                .screenState
                .flowWithLifecycle(lifecycle)
                .collect(::uiForState)
        }

        viewBinding.sportsList.layoutManager = rvLayoutManager
        viewBinding.sportsList.adapter = this.sportsListAdapter

        viewModel.fetchSports()
    }

    private fun uiForState(state: ScreenState) {
        when (state) {
            is ScreenState.Error -> {
                with(viewBinding) {
                    sportsList.visibility = View.GONE
                    loading.visibility = View.GONE
                    message.text = state.message
                    message.visibility = View.VISIBLE
                }
            }

            ScreenState.Loading -> {
                with(viewBinding) {
                    sportsList.visibility = View.GONE
                    loading.visibility = View.VISIBLE
                    message.visibility = View.GONE
                }
            }

            ScreenState.NoInternet -> {
                with(viewBinding) {
                    sportsList.visibility = View.GONE
                    loading.visibility = View.GONE
                    message.text = "No internet connection"
                    message.visibility = View.VISIBLE
                }
            }

            is ScreenState.Sports -> {
                with(viewBinding) {
                    sportsList.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                    message.visibility = View.GONE

                    sportsList.post { sportsListAdapter.updateData(state.sportsViewItems) }
                }
            }
        }
    }
}