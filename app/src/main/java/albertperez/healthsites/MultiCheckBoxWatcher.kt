package albertperez.healthsites

import android.widget.CheckBox
import android.widget.CompoundButton

class MultiCheckBoxWatcher {
    private var callback: CheckBoxWatcherWithInstance? = null
    fun setCallback(callback: CheckBoxWatcherWithInstance?) {
        this.callback = callback
    }

    fun registerCheckBox(checkBox: CheckBox): MultiCheckBoxWatcher {
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            callback!!.onCheckedChanged(buttonView, isChecked)
        }
        return this
    }

    interface CheckBoxWatcherWithInstance {
        fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
    }
}