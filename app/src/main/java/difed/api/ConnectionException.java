/*
   Copyright 2011 
   
   DIFED

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package difed.api;

import difed.despertadoramigo.R;


public class ConnectionException extends Exception {
    
    public static enum ConnectionError {
        NOT_FOUND(404, R.string.error_not_found),
        UNAUTHORIZED(401, R.string.error_unahthorized),
        NO_CONNECTION(0, R.string.error_no_connection),
        CONNECTION_REJECTED(0, R.string.error_connection_rejected),
        NO_DATA(0, R.string.error_no_data),
        UNKNOWN_ERROR(0, R.string.error_unknown_error),
        READING_ERROR(0, R.string.error_reading_error);
          
        private int code;
        private int message;
        
        private ConnectionError(int code, int message) {
            this.code = code;
            this.message = message;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
        
        public void setMessage(int message) {
            this.message = message;
        }
        
        public int getMessage() {
            return message;
        }
        
        public static ConnectionError getErrorByCode(int code) {
            for (ConnectionError error : values()) {
                if (error.getCode() == code) {
                    return error;
                }
            }
            ConnectionError error = UNKNOWN_ERROR;
            error.setCode(code);
            return error;
        }
    }
    
    private static final long serialVersionUID = -4340746575509376119L;
    
    private ConnectionError error;
    
    public ConnectionException(ConnectionError error) {
        this.error = error;
    }
    
    public ConnectionError getError() {
        return error;
    }

}
