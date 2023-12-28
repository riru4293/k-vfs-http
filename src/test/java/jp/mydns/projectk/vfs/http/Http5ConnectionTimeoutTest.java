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
import java.time.Duration;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;
import test.Http5ConfigUtils;

/**
 * Test of class Http5ConnectionTimeout.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class Http5ConnectionTimeoutTest {

    /**
     * Test constructor. If argument is valid {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue() {

        assertThat(new Http5ConnectionTimeout(Json.createValue("PT59S")).getValue()).isEqualTo(Json.createValue("PT59S"));

    }

    /**
     * Test constructor. If argument is illegal {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_IllegalJsonValue() {

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5ConnectionTimeout(JsonValue.NULL))
                .withMessage("FileOption value of [%s] must be duration.", "http:connectionTimeout");

    }

    /**
     * Test constructor. If argument is valid {@code Duration}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_Duration() {

        assertThat(new Http5ConnectionTimeout(Duration.ofMillis(3)).getValue()).isEqualTo(Json.createValue("PT0.003S"));

    }

    /**
     * Test apply method.
     *
     * @since 1.0.0
     */
    @Test
    void testApply() throws FileSystemException {

        FileSystemOptions opts = new FileSystemOptions();

        new Http5ConnectionTimeout.Resolver().newInstance(Json.createValue("PT8H")).apply(opts);

        assertThat(extractValue(opts)).hasToString("PT8H");

        new Http5ConnectionTimeout.Resolver().newInstance(Json.createValue("PT0S")).apply(opts);

        assertThat(extractValue(opts)).isZero();

    }

    /**
     * Test {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode() {

        Http5ConnectionTimeout base = new Http5ConnectionTimeout(Duration.ZERO);
        Http5ConnectionTimeout same = new Http5ConnectionTimeout(Duration.ZERO);
        Http5ConnectionTimeout another = new Http5ConnectionTimeout(Duration.ofMillis(2L));

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

        var result = new Http5ConnectionTimeout(Duration.ZERO).toString();

        assertThat(result).isEqualTo("{\"http:connectionTimeout\":\"PT0S\"}");

    }

    Duration extractValue(FileSystemOptions opts) {

        var utils = new Http5ConfigUtils();

        return utils.getParam(opts, "http.connection.timeout");

    }
}
