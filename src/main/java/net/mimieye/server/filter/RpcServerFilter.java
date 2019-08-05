/*
 * MIT License
 *
 * Copyright (c) 2017-2019 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package net.mimieye.server.filter;


import net.mimieye.core.exception.NulsException;
import net.mimieye.core.exception.NulsRuntimeException;
import net.mimieye.core.log.Log;
import net.mimieye.model.ErrorData;
import net.mimieye.model.RpcClientResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Niels
 */
public class RpcServerFilter implements ContainerRequestFilter, ContainerResponseFilter, ExceptionMapper<Exception> {

    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //Enumeration<String> headerNames = request.getHeaderNames();
        //while (headerNames.hasMoreElements()) {
        //    String name = headerNames.nextElement();
        //    System.out.println(String.format("head name [%s], value [%s]", name, request.getHeader(name)));
        //}
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws UnsupportedEncodingException {
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    public Response toResponse(Exception e) {
        Log.error("RequestURI is " + request.getRequestURI(), e);
        RpcClientResult result;
        if (e instanceof NulsException) {
            NulsException exception = (NulsException) e;
            result = new RpcClientResult(false, exception.getErrorCode());
        } else if (e instanceof NulsRuntimeException) {
            NulsRuntimeException exception = (NulsRuntimeException) e;
            result = RpcClientResult.getFailed(new ErrorData(exception.getCode(), exception.getMessage()));
        } else {
            result = RpcClientResult.getFailed(e.getMessage());
        }

        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
}
