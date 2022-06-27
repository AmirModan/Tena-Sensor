package TenaSensor.Android;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface AWS_Interface {
    /**
     * Invoke the Lambda function "AndroidBackendLambdaFunction".
     * The function name is the method name.
     */
    @LambdaFunction
    AWS_Response TenaFunction1(AWS_Request request);
}
