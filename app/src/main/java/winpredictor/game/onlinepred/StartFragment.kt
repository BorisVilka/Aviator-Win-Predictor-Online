package winpredictor.game.onlinepred

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import winpredictor.game.onlinepred.databinding.FragmentStartBinding
import java.io.IOException


class StartFragment : Fragment() {

    private lateinit var binding:FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStartBinding.inflate(inflater,container,false)
        try {
            // get input stream
            val ims = requireContext().assets.open("bg.png")
            // load image as Drawable
            val d = Drawable.createFromStream(ims, null)
            // set image to ImageView
            binding.bg.setImageDrawable(d)
            ims.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        binding.id.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.navigate(R.id.fragmentGame)
        }
        return binding.root
    }


}