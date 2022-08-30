package com.courage.platform.schedule.client.invoke;

public interface ClientInvoke {

    String getServiceId();

    Object invoke(String serviceId, Object[] params) throws Exception;

    boolean isSameClassMethod(ClientInvoke invoker);

}
