package com.example.droiddesign.view.Everybody;

public interface InterfaceAuthCallback {
	/**
	 * Called when the authentication process completes successfully.
	 */
	void onSuccess();

	/**
	 * Called when the authentication process fails.
	 */
	void onFailure();
}
