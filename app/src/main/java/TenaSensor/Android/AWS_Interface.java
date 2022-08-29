package TenaSensor.Android;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/**
 * @author Amir Modan (amir5modan@gmail.com)
 * Interface which interacts with the AWS Lambda function
 *
 * Interfaces with AWS Lambda via:
 *  AWS_Request (Class) - Handles Requests made by the app to AWS
 *  AWS_Response (Class) - Handles Responses made to the app by AWS
 */

public interface AWS_Interface {
    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction
    AWS_Response TenaFunction(AWS_Request request);
}
