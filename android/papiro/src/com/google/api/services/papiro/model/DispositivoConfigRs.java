/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-05-23 17:46:09 UTC)
 * on 2013-06-04 at 16:04:01 UTC 
 * Modify at your own risk.
 */

package com.google.api.services.papiro.model;

/**
 * Model definition for DispositivoConfigRs.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class DispositivoConfigRs extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer anuncioChangeInterval;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer checkInterval;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String eventsLogFileLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer inactivityInterval;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer infoContactoFormLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer videoChangeInterval;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getAnuncioChangeInterval() {
    return anuncioChangeInterval;
  }

  /**
   * @param anuncioChangeInterval anuncioChangeInterval or {@code null} for none
   */
  public DispositivoConfigRs setAnuncioChangeInterval(java.lang.Integer anuncioChangeInterval) {
    this.anuncioChangeInterval = anuncioChangeInterval;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getCheckInterval() {
    return checkInterval;
  }

  /**
   * @param checkInterval checkInterval or {@code null} for none
   */
  public DispositivoConfigRs setCheckInterval(java.lang.Integer checkInterval) {
    this.checkInterval = checkInterval;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEventsLogFileLocation() {
    return eventsLogFileLocation;
  }

  /**
   * @param eventsLogFileLocation eventsLogFileLocation or {@code null} for none
   */
  public DispositivoConfigRs setEventsLogFileLocation(java.lang.String eventsLogFileLocation) {
    this.eventsLogFileLocation = eventsLogFileLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public DispositivoConfigRs setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getInactivityInterval() {
    return inactivityInterval;
  }

  /**
   * @param inactivityInterval inactivityInterval or {@code null} for none
   */
  public DispositivoConfigRs setInactivityInterval(java.lang.Integer inactivityInterval) {
    this.inactivityInterval = inactivityInterval;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getInfoContactoFormLocation() {
    return infoContactoFormLocation;
  }

  /**
   * @param infoContactoFormLocation infoContactoFormLocation or {@code null} for none
   */
  public DispositivoConfigRs setInfoContactoFormLocation(java.lang.Integer infoContactoFormLocation) {
    this.infoContactoFormLocation = infoContactoFormLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getVideoChangeInterval() {
    return videoChangeInterval;
  }

  /**
   * @param videoChangeInterval videoChangeInterval or {@code null} for none
   */
  public DispositivoConfigRs setVideoChangeInterval(java.lang.Integer videoChangeInterval) {
    this.videoChangeInterval = videoChangeInterval;
    return this;
  }

  @Override
  public DispositivoConfigRs set(String fieldName, Object value) {
    return (DispositivoConfigRs) super.set(fieldName, value);
  }

  @Override
  public DispositivoConfigRs clone() {
    return (DispositivoConfigRs) super.clone();
  }

}