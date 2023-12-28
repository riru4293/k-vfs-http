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
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;
import test.Http5ConfigUtils;

/**
 * Test of class Http5KeyStoreFile.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class Http5KeyStoreFileTest {

    private final Path file1Path;
    private final URI file1Uri;
    private final String file1;

    private final Path file2Path;
    private final URI file2Uri;
    private final String file2;

    public Http5KeyStoreFileTest() throws IOException {

        this.file1Path = Files.createTempFile(null, null);
        this.file1Uri = this.file1Path.toUri();
        this.file1 = this.file1Uri.toString();

        this.file2Path = Files.createTempFile(null, null);
        this.file2Uri = this.file2Path.toUri();
        this.file2 = this.file2Uri.toString();

    }

    /**
     * Test constructor. If argument is valid {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue() {

        assertThat(new Http5KeyStoreFile(Json.createValue(file1)).getValue())
                .isEqualTo(Json.createValue(file1));

    }

    /**
     * Test constructor. If argument is illegal {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_IllegalJsonValue() {

        assertThatIllegalArgumentException().isThrownBy(() -> new Http5KeyStoreFile(JsonValue.NULL))
                .withMessage("FileOption value of [http:keyStoreFileUri] must be convertible to URI,"
                        + " and further convertible to absolute path of local file.");

    }

    /**
     * Test constructor. If argument is valid {@code Path}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_Path() {

        assertThat(new Http5KeyStoreFile(file1Path).getValue()).isEqualTo(Json.createValue(file1));

    }

    /**
     * Test apply method.
     *
     * @since 1.0.0
     */
    @Test
    void testApply() throws FileSystemException, IOException {

        FileSystemOptions opts = new FileSystemOptions();

        new Http5KeyStoreFile.Resolver().newInstance(Json.createValue(file1)).apply(opts);

        assertThat(extractValue(opts)).isEqualTo(file1Path.toString());

        new Http5KeyStoreFile.Resolver().newInstance(Json.createValue(file2)).apply(opts);

        assertThat(extractValue(opts)).isEqualTo(file2Path.toString());

    }

    /**
     * Test {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode() {

        Http5KeyStoreFile base = new Http5KeyStoreFile(file1Path);
        Http5KeyStoreFile same = new Http5KeyStoreFile(file1Path);
        Http5KeyStoreFile another = new Http5KeyStoreFile(file2Path);

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

        var result = new Http5KeyStoreFile(file1Path).toString();

        assertThat(result).isEqualTo("{\"http:keyStoreFileUri\":\"%s\"}".formatted(file1));

    }

    String extractValue(FileSystemOptions opts) {

        var utils = new Http5ConfigUtils();

        return utils.getParam(opts, "http.keystoreFile");

    }
}
