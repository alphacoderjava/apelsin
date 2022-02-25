# apelsin
#POST method    **/api/v1/file -> param: MultipartFile

#GET method     **/api/v1/file/preview/{hashId} -> file preview by hashids

#GET method     **/api/v1/file/{hashid}

#GET method     **/api/v1/file/download/{hashId}  -> download file with original name

#GET method     **/api/v1/file  -> PARAM: fileName. returns a list of files whose file name is like fileName

#GET method     **/api/v1/file/filter -> params: size, size2. returns a list of files whose file size is between SIZE and SIZE2.

#GET method     **/api/v1/file/all -> returns a list of all files in the database

#GET method     **/api/v1/file/date -> params: date1, date2 (format yyyy-MM-dd). returns a list of files uploaded in two time intervals
