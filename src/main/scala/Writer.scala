import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}

import java.io.ByteArrayInputStream
import java.time.LocalDateTime

class Writer(s3client: AmazonS3, bucketName: String) {
  def write(fileName: String, data: Array[Byte]): Unit = {
    val inputstream = new ByteArrayInputStream(data)
    val metadata = new ObjectMetadata()

    metadata.addUserMetadata("upload-datetime", LocalDateTime.now().toString)
    s3client.putObject(
      new PutObjectRequest(bucketName, fileName, inputstream, metadata)
    )
  }
}
