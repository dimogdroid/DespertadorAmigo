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

package difed.util;

import android.util.Log;

public class CustomLog {
    
    private static final String TAG = "AMIGO";
    
    private static final int LOG_LEVEL = Log.DEBUG;
    private static final boolean VERBOSE = LOG_LEVEL <= Log.VERBOSE;
    private static final boolean DEBUG = LOG_LEVEL <= Log.DEBUG;
    private static final boolean INFO = LOG_LEVEL <= Log.INFO;
    private static final boolean WARN = LOG_LEVEL <= Log.WARN;
    private static final boolean ERROR = LOG_LEVEL <= Log.ERROR;

    public static void verbose(String source, String msg) {
        if (VERBOSE) {
            Log.v(TAG, source + ": " + msg);
        } 
    }

    public static void debug(String source, String msg) {
        if (DEBUG) {
            Log.d(TAG, source + ": " + msg);
        }       
    }

    public static void info(String source, String msg) {
        if (INFO) {
            Log.i(TAG, source + ": " + msg);
        }       
    }

    public static void warn(String source, String msg) {
        if (WARN) {
            Log.w(TAG, source + ": " + msg);
        }       
    }

    public static void error(String source, String msg) {
        if (ERROR) {
            Log.e(TAG, source + ": " + msg);
        }       
    }
}
