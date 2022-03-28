package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout

class ClassesShimmerAdapter(var context: Context, var isShimmer: Boolean, var count: Int) :
    RecyclerView.Adapter<ClassesShimmerAdapter.HomePageDevShimmerHolder>() {

    class HomePageDevShimmerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shimmerFrameLayout: ShimmerFrameLayout =
            view.findViewById(R.id.shimmerClassFrameLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageDevShimmerHolder {
        return HomePageDevShimmerHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.recycler_shimmer_classes_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomePageDevShimmerHolder, position: Int) {
        if (isShimmer)
            holder.shimmerFrameLayout.startShimmerAnimation()
        else
            holder.shimmerFrameLayout.stopShimmerAnimation()
    }

    override fun getItemCount() = count
}