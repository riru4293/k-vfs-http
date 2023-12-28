/*
 * Copyright (c) 2023, Project-K
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
package jp.mydns.projectk.vfs.http;

import jakarta.json.Json;
import jakarta.json.JsonValue;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import static org.apache.commons.vfs2.UserAuthenticationData.DOMAIN;
import static org.apache.commons.vfs2.UserAuthenticationData.PASSWORD;
import static org.apache.commons.vfs2.UserAuthenticationData.USERNAME;
import org.apache.commons.vfs2.UserAuthenticator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;
import test.Http5ConfigUtils;

/**
 * Test of class Http5ProxyAuthenticator.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class Http5ProxyAuthenticatorTest {

    /**
     * Test constructor. If argument is valid {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue() {

        assertThat(new Http5ProxyAuthenticator(Json.createObjectBuilder().add("id", "i").add("password", "p")
                .add("domain", "d").build()).getValue())
                .isEqualTo(Json.createObjectBuilder().add("id", "i").add("password", "p").add("domain", "d").build());

    }

    /**
     * Test constructor. If argument is illegal {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_IllegalJsonValue() {

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5ProxyAuthenticator(JsonValue.NULL))
                .withMessage("FileOption value of [http:keyStoreFileUri] must be convertible to JSON object.");

    }

    /**
     * Test constructor. If argument is valid {@code String}, {@code String}, {@code String}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_String_String_String() {

        assertThat(new Http5ProxyAuthenticator("i", "p", "d").getValue())
                .isEqualTo(Json.createObjectBuilder().add("id", "i").add("password", "p").add("domain", "d").build());

    }

    /**
     * Test apply method.
     *
     * @since 1.0.0
     */
    @Test
    void testApply() throws FileSystemException {

        FileSystemOptions opts = new FileSystemOptions();

        new Http5ProxyAuthenticator.Resolver().newInstance(Json.createObjectBuilder().add("id", "u").add("password", "p")
                .add("domain", "d").build()).apply(opts);

        var ua = extractValue(opts);
        var uad = ua.requestAuthentication(new UserAuthenticationData.Type[]{DOMAIN, USERNAME, PASSWORD});

        assertThat(uad.getData(USERNAME)).containsExactly('u');
        assertThat(uad.getData(PASSWORD)).containsExactly('p');
        assertThat(uad.getData(DOMAIN)).containsExactly('d');

        new Http5ProxyAuthenticator.Resolver().newInstance(Json.createObjectBuilder().add("id", "u2").add("password", "p2")
                .add("domain", "d2").build()).apply(opts);

        var ua2 = extractValue(opts);
        var uad2 = ua2.requestAuthentication(new UserAuthenticationData.Type[]{DOMAIN, USERNAME, PASSWORD});

        assertThat(uad2.getData(USERNAME)).containsExactly('u', '2');
        assertThat(uad2.getData(PASSWORD)).containsExactly('p', '2');
        assertThat(uad2.getData(DOMAIN)).containsExactly('d', '2');

    }

    /**
     * Test {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode() {

        Http5ProxyAuthenticator base = new Http5ProxyAuthenticator("i", "p", "d");
        Http5ProxyAuthenticator same = new Http5ProxyAuthenticator("i", "p", "d");
        Http5ProxyAuthenticator another = new Http5ProxyAuthenticator("i", "p", null);

        assertThat(base).hasSameHashCodeAs(same).isEqualTo(same)
                .doesNotHaveSameHashCodeAs(another).isNotEqualTo(another);

    }

    /**
     * Test of toString method.
     *
     * @since 1.0.0
     */
    @Test
    void testToString() {

        var result = new Http5ProxyAuthenticator("i", "p", "d").toString();

        assertThat(result).isEqualTo("{\"http:proxyAuthenticator\":{\"id\":\"i\",\"password\":\"p\",\"domain\":\"d\"}}");

    }

    UserAuthenticator extractValue(FileSystemOptions opts) {

        var utils = new Http5ConfigUtils();

        return utils.getParam(opts, "proxyAuthenticator");

    }
}
