/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.arquillian.graphene.ftest.enricher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.enricher.FieldAccessValidatorEnricher;
import org.jboss.arquillian.graphene.ftest.Resource;
import org.jboss.arquillian.graphene.ftest.Resources;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * @author Juraj Huska
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TestFieldAccessValidatorEnricher {

    @Drone
    private WebDriver browser;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @ArquillianResource
    private URL contextRoot;

    private static final Logger LOGGER = Logger.getLogger(FieldAccessValidatorEnricher.class.getName());
    private static final String PART_OF_WARNING = "Public field";

    private static Handler logHandler;
    private static ByteArrayOutputStream logOutput;

    @Deployment
    public static WebArchive createTestArchive() {
        return Resources.inCurrentPackage().all().buildWar("test.war");
    }

    @Before
    public void loadPage() {
        Resource.inCurrentPackage().find("sample.html").loadPage(browser, contextRoot);
    }

    @BeforeClass
    public static void redirectLoggerOutput() throws IOException {
        logOutput = new ByteArrayOutputStream();
        logHandler = new StreamHandler(logOutput, new SimpleFormatter());
        logHandler.setLevel(Level.ALL);
        LOGGER.addHandler(logHandler);
    }

    @Test
    public void testFooBar() throws IOException {
        try {
            logHandler.flush();

            Assert.assertFalse("There should be no warnign produced by FieldAccessValidatorEnricher!", logOutput.toString().contains(PART_OF_WARNING));
        } finally {
            logHandler.close();
            LOGGER.removeHandler(logHandler);
        }
    }

}
