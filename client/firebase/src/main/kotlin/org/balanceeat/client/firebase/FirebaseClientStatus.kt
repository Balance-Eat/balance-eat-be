package org.balanceeat.client.firebase

import org.balanceeat.common.Status

enum class FirebaseClientStatus(override val message: String): Status {
    FIREBASE_ERROR("파이어베이스 서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),

    /*
    /**
 * Error codes that can be raised by the Cloud Messaging APIs.
 */
public enum MessagingErrorCode {

  /**
   * APNs certificate or web push auth key was invalid or missing.
   */
  THIRD_PARTY_AUTH_ERROR,

  /**
   * One or more arguments specified in the request were invalid.
   */
  INVALID_ARGUMENT,

  /**
   * Internal server error.
   */
  INTERNAL,

  /**
   * Sending limit exceeded for the message target.
   */
  QUOTA_EXCEEDED,

  /**
   * The authenticated sender ID is different from the sender ID for the registration token.
   */
  SENDER_ID_MISMATCH,

  /**
   * Cloud Messaging service is temporarily unavailable.
   */
  UNAVAILABLE,

  /**
   * App instance was unregistered from FCM. This usually means that the token used is no longer
   * valid and a new one must be used.
   */
  UNREGISTERED,
}
    */

}