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
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import static jakarta.json.stream.JsonCollectors.toJsonArray;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import jp.mydns.projectk.vfs.AbstractFileOption;
import jp.mydns.projectk.vfs.FileOption;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.http5.Http5FileSystemConfigBuilder;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

/**
 * Cookies to add to HTTP request.
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class and JSON can be converted bidirectionally.</li>
 * <li>Can reflect this class on the {@link FileSystemOptions}.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see Http5FileSystemConfigBuilder#getCookies(org.apache.commons.vfs2.FileSystemOptions)
 */
@FileOption.Name("http:cookies")
public class Http5Cookies extends AbstractFileOption {

    private final List<CookieSource> values;

    /**
     * Constructor.
     *
     * @param values option values
     * @throws NullPointerException if {@code values} is {@code null}, or if {@code values} contains {@code null}.
     * @since 1.0.0
     */
    public Http5Cookies(List<CookieSource> values) {

        this.values = List.copyOf(values);

    }

    /**
     * Constructor.
     * <p>
     * <b>Example:</b>
     * <pre><code>
     * [
     *     {
     *         "name"      : "required"
     *        ,"value"     : "nullable"
     *        ,"domain"    : "nullable"
     *        ,"path"      : "nullable"
     *        ,"isOnlyHttp": false
     *        ,"isSecure"  : false
     *        ,"creationDateTime": "2000-01-01T00:00:00"
     *        ,"expiryDateTime"  : "2999-12-31T23:59:59"
     *        ,"attributes": [ { "name": "required", "value": "nullable" } ]
     *     }
     * ]
     * </code></pre> Only name element is required.
     *
     * @param values option values
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} is not convertible to type
     * {@code List<org.apache.hc.client5.http.cookie.Cookie>}
     * @since 1.0.0
     */
    public Http5Cookies(JsonValue values) {

        Objects.requireNonNull(values);

        try {

            this.values = values.asJsonArray().stream().map(JsonValue::asJsonObject).map(CookieSource::new).toList();

        } catch (ClassCastException | NullPointerException | DateTimeParseException | IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "FileOption value of [http:cookies] must be list of cookie.", ex);

        }

    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>JSON Format</b>
     * <br>Note: If the value is {@code null}, the value is not output including the key
     * <pre><code>
     * [
     *     {
     *         "name"      : "required"
     *        ,"value"     : "nullable"
     *        ,"domain"    : "nullable"
     *        ,"path"      : "nullable"
     *        ,"isOnlyHttp": false
     *        ,"isSecure"  : false
     *        ,"creationDateTime": "2000-01-01T00:00:00"
     *        ,"expiryDateTime"  : "2999-12-31T23:59:59"
     *        ,"attributes": [ { "name": "required", "value": "nullable" } ]
     *     }
     * ]
     * </code></pre>
     *
     * @return value of JSON representation
     * @since 1.0.0
     */
    @Override
    public JsonValue getValue() {

        return values.stream().map(CookieSource::toJsonObject).collect(toJsonArray());

    }

    /**
     * {@inheritDoc}
     *
     * @param opts the {@code FileSystemOptions}. This value will be modified.
     * @throws NullPointerException if {@code opts} is {@code null}
     * @since 1.0.0
     */
    @Override
    public void apply(FileSystemOptions opts) throws FileSystemException {

        Objects.requireNonNull(opts);

        Http5FileSystemConfigBuilder.getInstance().setCookies(opts,
                values.stream().map(CookieSource::toCookie).toArray(Cookie[]::new));

    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {

        return Objects.hash(getName(), getValue());

    }

    /**
     * Indicates that other object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if equals ,otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {

        return other instanceof Http5Cookies o
                && Objects.equals(getName(), o.getName())
                && Objects.equals(getValue(), o.getValue());

    }

    /**
     * Returns a string representation of this.
     *
     * @return string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {

        return Json.createObjectBuilder().add(getName(), getValue()).build().toString();

    }

    /**
     * Resolver for {@link Http5Cookies} instance from JSON.
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * <li>Implementations of this interface must be able to construct instances using {@link ServiceLoader}.</li>
     * <li>This class must be able to construct an instance of {@code FileOption} from the JSON representing
     * {@code FileOption}.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class Resolver implements FileOption.Resolver {
    }

    /**
     * Source value for creation {@link Cookie}.
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * <li>This class and JSON can be converted bidirectionally.</li>
     * <li>This class convertible to {@code Cookie}.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class CookieSource {

        private static final String ATTR_NAME = "name";
        private static final String ATTR_VALUE = "value";
        private static final String ATTR_DOMAIN = "domain";
        private static final String ATTR_PATH = "path";
        private static final String ATTR_ONLYHTTP = "isOnlyHttp";
        private static final String ATTR_SECURE = "isSecure";
        private static final String ATTR_CREATION = "creationDateTime";
        private static final String ATTR_EXPIRY = "expiryDateTime";
        private static final String ATTR_ATTRS = "attributes";

        private final String name;
        private final String value;
        private final String domain;
        private final String path;
        private final Boolean onlyHttp;
        private final Boolean secure;
        private final LocalDateTime creation;
        private final LocalDateTime expiry;
        private final Map<String, String> attributes;

        /**
         * Constructor.
         * <p>
         * <b>Example:</b>
         * <pre><code>
         * {
         *     "name"      : "required"
         *    ,"value"     : "nullable"
         *    ,"domain"    : "nullable"
         *    ,"path"      : "nullable"
         *    ,"isOnlyHttp": false
         *    ,"isSecure"  : false
         *    ,"creationDateTime": "2000-01-01T00:00:00"
         *    ,"expiryDateTime"  : "2999-12-31T23:59:59"
         *    ,"attributes": [ { "name": "required", "value": "nullable" } ]
         * }
         * </code></pre> Only name element is required.
         *
         *
         * @param value JSON representation of {@code Cookie}
         * @throws NullPointerException if {@code value} is {@code null}, or if "{@value ATTR_NAME}" element is
         * {@code null}.
         * @throws ClassCastException if {@code value} is in an unexpected format
         * @throws DateTimeParseException if "{@value ATTR_CREATION}" element or "{@value ATTR_EXPIRY}" element value
         * format is malformed as {@code LocalDateTime}. An example of a valid format is "2007-12-03T10:15:30".
         * @throws IllegalArgumentException if duplicated the "{@value ATTR_NAME}" element in "{@value ATTR_ATTRS}"
         * element.
         * @since 1.0.0
         */
        public CookieSource(JsonObject value) {

            Objects.requireNonNull(value);

            this.name = value.getString(ATTR_NAME);
            this.value = value.getString(ATTR_VALUE, null);
            this.domain = value.getString(ATTR_DOMAIN, null);
            this.path = value.getString(ATTR_PATH, null);
            this.onlyHttp = extractBoolean(ATTR_ONLYHTTP, value);
            this.secure = extractBoolean(ATTR_SECURE, value);
            this.creation = extractLocalDateTime(ATTR_CREATION, value);
            this.expiry = extractLocalDateTime(ATTR_EXPIRY, value);
            this.attributes = extractAttributes(ATTR_ATTRS, value);

        }

        private Boolean extractBoolean(String name, JsonObject value) {

            return Optional.of(value).filter(v -> v.containsKey(name)).map(v -> v.getBoolean(name)).orElse(null);

        }

        private LocalDateTime extractLocalDateTime(String name, JsonObject value) {

            return Optional.ofNullable(value.getString(name, null)).map(LocalDateTime::parse).orElse(null);

        }

        private Map<String, String> extractAttributes(String name, JsonObject value) {

            Function<JsonArray, Map<String, String>> toMap = a -> {

                var m = new LinkedHashMap<String, String>();

                for (var v : a) {

                    Entry<String, String> e = extractAttribute(v.asJsonObject());

                    if (m.containsKey(e.getKey())) {

                        throw new IllegalArgumentException("Cookie attribute names must not be duplicated.");

                    }

                    m.put(e.getKey(), e.getValue());

                }

                return Collections.unmodifiableMap(m);

            };

            return Optional.of(value).filter(v -> v.containsKey(name))
                    .map(v -> v.getJsonArray(name)).map(toMap).orElse(null);

        }

        private Entry<String, String> extractAttribute(JsonObject value) {

            return new SimpleImmutableEntry<>(value.getString(ATTR_NAME), value.getString(ATTR_VALUE, null));

        }

        /**
         * Get all values as one JSON. The return value must be able to reproduce instance via the constructor.
         * <p>
         * <b>JSON Format</b>
         * <br>Note: If the value is {@code null}, the value is not output including the key
         * <pre><code>
         * {
         *     "name"      : "required"
         *    ,"value"     : "nullable"
         *    ,"domain"    : "nullable"
         *    ,"path"      : "nullable"
         *    ,"isOnlyHttp": false
         *    ,"isSecure"  : false
         *    ,"creationDateTime": "2000-01-01T00:00:00"
         *    ,"expiryDateTime"  : "2999-12-31T23:59:59"
         *    ,"attributes": [ { "name": "required", "value": "nullable" } ]
         * }
         * </code></pre>
         *
         * @return value of JSON representation
         * @since 1.0.0
         */
        public JsonObject toJsonObject() {

            Function<LocalDateTime, String> formatDateTime = v -> v.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Function<Entry<String, String>, JsonObject> formatAttr = e -> {

                var builder = Json.createObjectBuilder().add(ATTR_NAME, e.getKey());

                Optional.ofNullable(e.getValue()).ifPresent(v -> builder.add(ATTR_VALUE, v));

                return builder.build();

            };

            var builder = Json.createObjectBuilder().add(ATTR_NAME, name);

            Optional.ofNullable(value).ifPresent(v -> builder.add(ATTR_VALUE, v));
            Optional.ofNullable(domain).ifPresent(v -> builder.add(ATTR_DOMAIN, v));
            Optional.ofNullable(path).ifPresent(v -> builder.add(ATTR_PATH, v));
            Optional.ofNullable(onlyHttp).ifPresent(v -> builder.add(ATTR_ONLYHTTP, v));
            Optional.ofNullable(secure).ifPresent(v -> builder.add(ATTR_SECURE, v));
            Optional.ofNullable(creation).map(formatDateTime).ifPresent(v -> builder.add(ATTR_CREATION, v));
            Optional.ofNullable(expiry).map(formatDateTime).ifPresent(v -> builder.add(ATTR_EXPIRY, v));
            Optional.ofNullable(attributes).ifPresent(v -> builder.add(ATTR_ATTRS,
                    Json.createArrayBuilder(v.entrySet().stream().map(formatAttr).toList())));

            return builder.build();

        }

        /**
         * Build the {@code Cookie}.
         *
         * @return the {@code Cookie}
         * @since 1.0.0
         */
        public Cookie toCookie() {

            var cookie = new BasicClientCookie(name, value);

            Optional.ofNullable(domain).ifPresent(cookie::setDomain);
            Optional.ofNullable(path).ifPresent(cookie::setPath);
            Optional.ofNullable(onlyHttp).ifPresent(cookie::setHttpOnly);
            Optional.ofNullable(secure).ifPresent(cookie::setSecure);
            Optional.ofNullable(creation).map(v -> v.toInstant(ZoneOffset.UTC)).ifPresent(cookie::setCreationDate);
            Optional.ofNullable(expiry).map(v -> v.toInstant(ZoneOffset.UTC)).ifPresent(cookie::setExpiryDate);
            Optional.ofNullable(attributes).ifPresent(m -> m.entrySet()
                    .forEach(e -> cookie.setAttribute(e.getKey(), e.getValue())));

            return cookie;

        }

    }

}
