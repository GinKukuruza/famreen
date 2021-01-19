package com.example.famreen.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.famreen.R
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.SearchAdapter
import com.example.famreen.application.items.SearchItem
import com.example.famreen.application.items.SearchViewItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.utils.observers.UpdateObserver
import com.example.famreen.application.viewmodels.SearchViewModel
import com.example.famreen.databinding.FragmentSearchBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.extensions.set

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel = SearchViewModel()
    private var mSearchAdapter: SearchAdapter? = null
    private lateinit var mBinding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        mBinding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner,androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> {

                }
                is States.LoadingState -> {

                }
                is States.ErrorState -> {
                    Toast.makeText(requireContext(),it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
        val position = AppPreferences.getProvider()!!.readSearchEngine()
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(requireContext(), R.array.search_engine, R.layout.spinner_search_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerSearchSearchEngines.adapter = adapter
        mBinding.spinnerSearchSearchEngines.setSelection(position)
        mBinding.spinnerSearchSearchEngines.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                AppPreferences.getProvider()!!.writeSearchEngine(selectedItemPosition)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        updateAdapter(viewModel.getSearchList())
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
    }

    private fun updateAdapter(items: List<SearchItem>) {
        updateView()
        if (mSearchAdapter == null) {
            mSearchAdapter = SearchAdapter(requireContext(),
                object : UpdateObserver{
                    override fun update() {
                        updateView()
                    }
                },
                items)
            mBinding.rvSearch.adapter = mSearchAdapter
        } else mSearchAdapter!!.notifyDataSetChanged()
    }

    fun updateView() {
        val searchViewItem = SearchViewItem()
        val name = AppPreferences.getProvider()!!.readSearchBrowserName()
        val packageName = AppPreferences.getProvider()!!.readSearchPackageBrowserName()
        searchViewItem.browserName = name
        for (item in viewModel.getSearchList()) {
            if (packageName == item.packageName) {
                mBinding.ivSearchImage.setImageDrawable(item.image)
                break
            }
        }
        mBinding.item = searchViewItem
    }
}