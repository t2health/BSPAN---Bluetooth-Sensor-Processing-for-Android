package com.t2.compassionUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ToggledButton extends Button {

	private boolean isChecked = false;
	private int[] initialState;
	
	public ToggledButton(Context context) {
		super(context);
		this.init();
	}

	public ToggledButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public ToggledButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() {
		initialState = super.getDrawableState();
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		this.refreshDrawableState();
	}

	public boolean isChecked() {
		return isChecked;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		int[] states;
		
		if(this.isChecked()) {
			states = Button.PRESSED_WINDOW_FOCUSED_STATE_SET;
		} else {
			if(super.hasFocus()) {
				states = super.onCreateDrawableState(extraSpace);
			} else {
				states = initialState;
			}
		}
		
		return states;
	}
	
}
