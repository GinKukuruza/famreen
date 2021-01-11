package com.example.famreen.application.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.viewmodels.LoginViewModel
import com.example.famreen.databinding.FragmentLoginBinding
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.db.User
import com.example.famreen.utils.set
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class LoginFragment : Fragment() {
    @Inject lateinit var viewModel: LoginViewModel
    private val rcSignIn = 1
    private lateinit var navController: NavController
    private lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentLoginBinding.inflate(inflater)
        mBinding.btLoginGoogleIn.setOnClickListener {
            FirebaseProvider.exit()
                signInWithGoogle()
        }
        mBinding.btLoginGithubIn.setOnClickListener {
            FirebaseProvider.exit()
                signInWithGitHub()
        }
        mBinding.btLoginSignIn.setOnClickListener {
            viewModel.customLogin(mBinding.etLoginEmail.text.toString(), mBinding.etLoginPassword.text.toString())
        }
        mBinding.tvLoginChangePassword.setOnClickListener { navController.navigate(R.id.action_fragmentLogin_to_changePasswordFragment) }
        mBinding.tvLoginDeleteAccount.setOnClickListener {  viewModel.deleteAccount() }
        mBinding.btToRegisterEmail.setOnClickListener {  navController.navigate(R.id.action_fragmentLogin_to_registrationFragment)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> {
                    mBinding.etLoginEmail.setText("")
                    mBinding.etLoginPassword.setText("")
                }
                is States.LoadingState -> {

                }
                is States.ErrorState -> {
                    Toast.makeText(requireContext(),it.msg,Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@LoginFragment)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    viewModel.authWithGoogle(account)
                }
            } catch (e: ApiException) {
                viewModel.state.set(States.ErrorState("Api troubles, please report it"))
            }
        }
    }

    private fun signInWithGitHub() {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("login", "")
        val scopes: List<String> = object : ArrayList<String>() {
            init {
                add("login")
            }
        }
        provider.scopes = scopes
        val pendingResultTask = FirebaseConnection.firebaseAuth?.pendingAuthResult
        pendingResultTask
            ?.addOnSuccessListener {
                authResult: AuthResult? ->
                viewModel.successAuth(authResult) }
            ?.addOnFailureListener {
                    e: Exception? -> viewModel.catchException(e) }
            ?: FirebaseConnection.firebaseAuth
                ?.startActivityForSignInWithProvider( /* activity= */requireActivity(), provider.build())
                ?.addOnSuccessListener { authResult: AuthResult? ->
                    viewModel.successAuth(authResult)
                }
                ?.addOnFailureListener {
                        e: Exception? -> viewModel.catchException(e) }
    }
    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().state.set(States.UserState(user))
        when(user){
            is User -> {
                mBinding.tvLoginChangePassword.visibility = View.VISIBLE
                mBinding.tvLoginDeleteAccount.visibility = View.VISIBLE
            }
            is FirebaseUser -> {
                mBinding.tvLoginChangePassword.visibility = View.GONE
                mBinding.tvLoginDeleteAccount.visibility = View.VISIBLE
            }
            is EmptyUser -> {
                mBinding.tvLoginChangePassword.visibility = View.GONE
                mBinding.tvLoginDeleteAccount.visibility = View.GONE
            }
            is UninitializedUser -> {
                viewModel.getUser()
            }
        }
    }
}