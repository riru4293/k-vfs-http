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
import java.util.List;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;
import test.Http5ConfigUtils;

/**
 * Test of class Http5TlsVersions.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class Http5TlsVersionsTest {

    /**
     * Test constructor. If argument is valid {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue() {

        assertThat(new Http5TlsVersions(Json.createArrayBuilder().add("V_1_3").add("V_1_1").build()).getValue())
                .isEqualTo(Json.createValue("V_1_3,V_1_1"));

    }

    /**
     * Test constructor. If argument is illegal {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_IllegalJsonValue() {

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5TlsVersions(JsonValue.NULL))
                .withMessage("FileOption value of [http:tlsVersions] must be either [V_1_0, V_1_1, V_1_2, V_1_3].");

    }

    /**
     * Test constructor. If argument is valid {@code List<String>}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_List_String() {

        assertThat(new Http5TlsVersions(List.of("V_1_1", "V_1_2", "V_1_3")).getValue())
                .isEqualTo(Json.createValue("V_1_1,V_1_2,V_1_3"));

    }

    /**
     * Test apply method.
     *
     * @since 1.0.0
     */
    @Test
    void testApply() throws FileSystemException {

        FileSystemOptions opts = new FileSystemOptions();

        new Http5TlsVersions.Resolver().newInstance(Json.createArrayBuilder().add("V_1_1").add("V_1_2").build()).apply(opts);

        assertThat(extractValue(opts)).isEqualTo("V_1_1,V_1_2");

        new Http5TlsVersions.Resolver().newInstance(Json.createArrayBuilder().add("V_1_2").add("V_1_3").build()).apply(opts);

        assertThat(extractValue(opts)).isEqualTo("V_1_2,V_1_3");

    }

    /**
     * Test {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode() {

        Http5TlsVersions base = new Http5TlsVersions(List.of());
        Http5TlsVersions same = new Http5TlsVersions(List.of());
        Http5TlsVersions another = new Http5TlsVersions(List.of("V_1_1", "V_1_2", "V_1_3"));

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

        var result = new Http5TlsVersions(List.of("V_1_1", "V_1_2", "V_1_3")).toString();

        assertThat(result).isEqualTo("{\"http:tlsVersions\":\"V_1_1,V_1_2,V_1_3\"}");

    }

    String extractValue(FileSystemOptions opts) {

        var utils = new Http5ConfigUtils();

        return utils.getParam(opts, "tlsVersions");

    }
}
