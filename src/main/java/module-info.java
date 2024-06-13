/*
 * Copyright (c) 2024, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Project-K VFS HTTP implements.
 *
 * @provides jp.mydns.projectk.vfs.FileOption.Resolver
 * @uses jp.mydns.projectk.vfs.FileOption.Resolver
 *
 * @since 1.0.0
 */
module jp.mydns.projectk.vfs.http {
    requires jp.mydns.projectk.vfs;
    requires commons.vfs2;
    requires commons.logging;
    requires org.apache.commons.lang3;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires jakarta.json;
    uses jp.mydns.projectk.vfs.FileOption.Resolver;
    provides jp.mydns.projectk.vfs.FileOption.Resolver with
            jp.mydns.projectk.vfs.http.Http5ConnectionTimeout.Resolver
          , jp.mydns.projectk.vfs.http.Http5Cookies.Resolver
          , jp.mydns.projectk.vfs.http.Http5FollowRedirect.Resolver
          , jp.mydns.projectk.vfs.http.Http5HostnameVerification.Resolver
          , jp.mydns.projectk.vfs.http.Http5KeepAlive.Resolver
          , jp.mydns.projectk.vfs.http.Http5KeyStoreFile.Resolver
          , jp.mydns.projectk.vfs.http.Http5KeyStoreType.Resolver
          , jp.mydns.projectk.vfs.http.Http5MaxConnectionsPerHost.Resolver
          , jp.mydns.projectk.vfs.http.Http5MaxTotalConnections.Resolver
          , jp.mydns.projectk.vfs.http.Http5PreemptiveAuthentication.Resolver
          , jp.mydns.projectk.vfs.http.Http5ProxyAuthenticator.Resolver
          , jp.mydns.projectk.vfs.http.Http5ProxyHost.Resolver
          , jp.mydns.projectk.vfs.http.Http5ProxyPort.Resolver
          , jp.mydns.projectk.vfs.http.Http5ProxyScheme.Resolver
          , jp.mydns.projectk.vfs.http.Http5SocketTimeout.Resolver
          , jp.mydns.projectk.vfs.http.Http5TlsVersions.Resolver
          , jp.mydns.projectk.vfs.http.Http5UrlCharset.Resolver
          , jp.mydns.projectk.vfs.http.Http5UserAgent.Resolver;
    exports jp.mydns.projectk.vfs.http;
}
