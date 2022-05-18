package org.tensorflow.lite.examples.poseestimation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class HistoryListAdapter(val context: Context, val postureList: ArrayList<Posture>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */

        val view : View
        val holder : ViewHolder

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.history_list_item, null)
            holder = ViewHolder()
            holder.view_image1 = view.findViewById(R.id.posturePhotoImg)
            holder.view_text1 = view.findViewById(R.id.posture_number)
            holder.view_text2 = view.findViewById(R.id.date)
            holder.view_text3 = view.findViewById(R.id.correct_score)

            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val item = postureList[position]
        val resourceId = context.resources.getIdentifier(item.image, "drawable", context.packageName)
        holder.view_image1?.setImageResource(resourceId)
        holder.view_text1?.text = item.posture
        holder.view_text2?.text = item.date
        holder.view_text3?.text = item.score

        return view
    }

    override fun getItem(p0: Int): Any {
        return postureList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return postureList.size
    }

    private class ViewHolder {
        var view_image1 : ImageView? = null
        var view_text1 : TextView? = null
        var view_text2 : TextView? = null
        var view_text3 : TextView? = null
    }
}