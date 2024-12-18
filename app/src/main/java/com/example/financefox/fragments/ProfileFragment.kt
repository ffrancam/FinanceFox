package com.example.financefox.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.financefox.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileFragment : Fragment() {

    // Binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException("User not logged in")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Texts
        binding.profileName.setText(user.displayName ?: "")
        binding.profileEmail.setText(user.email ?: "")

        binding.editProfileBtn.setOnClickListener {
            updateProfile(view)
        }
    }

    private fun updateProfile(view: View?) {
        // updated variables
        val newName = binding.profileName.text.toString().trim()
        val newEmail = binding.profileEmail.text.toString().trim()
        val pwd = binding.profilePwd.text.toString().trim()

        // Check if there is an empty field
        if (newName.isEmpty() || newEmail.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        // Check is email is valid
        if (!isValidEmail(newEmail)) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }



        val credential = EmailAuthProvider.getCredential(user.email!!, pwd)
        user.reauthenticate(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    if (newName != user.displayName) {
                        val update = UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build()
                        user.updateProfile(update)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                                    binding.profilePwd.setText("")
                                } else {
                                    Toast.makeText(requireContext(), "An error occurred while updating profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    // Update email if it's different
                    if (newEmail != user.email) {
                        user.updateEmail(newEmail)
                            .addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                                    binding.profilePwd.setText("")

                                } else {
                                    Toast.makeText(requireContext(), "An error occurred while updating email", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}