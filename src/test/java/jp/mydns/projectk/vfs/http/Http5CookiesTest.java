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
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.time.Instant;
import java.util.List;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.hc.client5.http.cookie.Cookie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;
import test.Http5ConfigUtils;

/**
 * Test of class Http5Cookies.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class Http5CookiesTest {

    private final JsonArray cookies = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                    .add("name", "n1").add("value", "v1").add("domain", "d1").add("path", "p1")
                    .add("isOnlyHttp", true).add("isSecure", true)
                    .add("creationDateTime", "2000-01-01T00:00:00").add("expiryDateTime", "2999-12-31T23:59:59")
                    .add("attributes", Json.createArrayBuilder()
                            .add(Json.createObjectBuilder().add("name", "an1"))
                            .add(Json.createObjectBuilder().add("name", "an2").add("value", "av2"))
                    ))
            .add(Json.createObjectBuilder().add("name", "n2"))
            .build();

    /**
     * Test constructor. If argument is valid {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue() {

        assertThat(new Http5Cookies(cookies).getValue()).isEqualTo(cookies);

    }

    /**
     * Test constructor. If argument is illegal type.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_IllegalType() {

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5Cookies(JsonValue.NULL))
                .withMessage("FileOption value of [http:cookies] must be list of cookie.");

    }

    /**
     * Test constructor. If missing "name" element.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_MissingName() {

        var src = Json.createArrayBuilder().add(Json.createObjectBuilder()).build();

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5Cookies(src))
                .withMessage("FileOption value of [http:cookies] must be list of cookie.");

    }

    /**
     * Test constructor. If duplicates "name" element in "attributes" element.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_DuplicateAttributeName() {

        var src = Json.createArrayBuilder().add(Json.createObjectBuilder()
                .add("name", "n")
                .add("attributes", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder().add("name", "an"))
                        .add(Json.createObjectBuilder().add("name", "an")))).build();

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5Cookies(src))
                .withMessage("FileOption value of [http:cookies] must be list of cookie.");

    }

    /**
     * Test constructor. If argument is valid {@code List<CookieSource>}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_List_CookieSource() {

        var c1 = new Http5Cookies.CookieSource(cookies.get(0).asJsonObject());
        var c2 = new Http5Cookies.CookieSource(cookies.get(1).asJsonObject());

        assertThat(new Http5Cookies(List.of(c1, c2)).getValue()).isEqualTo(cookies);

    }

    /**
     * Test apply method.
     *
     * @since 1.0.0
     */
    @Test
    void testApply() throws FileSystemException {

        FileSystemOptions opts = new FileSystemOptions();

        new Http5Cookies.Resolver().newInstance(cookies).apply(opts);

        List<Cookie> results = List.of(extractValue(opts));

        assertThat(results).hasSize(2)
                .satisfies(r -> assertThat(r).first()
                        .returns("n1", Cookie::getName)
                        .returns("v1", Cookie::getValue)
                        .returns("d1", Cookie::getDomain)
                        .returns("p1", Cookie::getPath)
                        .returns(true, Cookie::isHttpOnly)
                        .returns(true, Cookie::isSecure)
                        .returns(Instant.parse("2000-01-01T00:00:00Z"), Cookie::getCreationInstant)
                        .returns(Instant.parse("2999-12-31T23:59:59Z"), Cookie::getExpiryInstant)
                        .returns(null, c -> c.getAttribute("an1"))
                        .returns("av2", c -> c.getAttribute("an2")))
                .satisfies(r -> assertThat(r).last()
                        .returns("n2", Cookie::getName));
    }

    /**
     * Test {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode() {

        Http5Cookies base = new Http5Cookies(JsonValue.EMPTY_JSON_ARRAY);
        Http5Cookies same = new Http5Cookies(JsonValue.EMPTY_JSON_ARRAY);
        Http5Cookies another = new Http5Cookies(cookies);

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

        var result = new Http5Cookies(cookies).toString();

        assertThat(result).isEqualTo("{\"http:cookies\":%s}".formatted(cookies));

    }

    Cookie[] extractValue(FileSystemOptions opts) {

        var utils = new Http5ConfigUtils();

        return utils.getParam(opts, "cookies");

    }

}
