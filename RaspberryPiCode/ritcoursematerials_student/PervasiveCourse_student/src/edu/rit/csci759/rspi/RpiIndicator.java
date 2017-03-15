package edu.rit.csci759.rspi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class RpiIndicator implements RpiIndicatorInterface {

	public static GpioController gpio = GpioFactory.getInstance();
	public static GpioPinDigitalOutput p1 = gpio.provisionDigitalOutputPin(
			RaspiPin.GPIO_27, "MyLED", PinState.LOW);
	public static GpioPinDigitalOutput p2 = gpio.provisionDigitalOutputPin(
			RaspiPin.GPIO_28, "MyLED", PinState.LOW);
	public static GpioPinDigitalOutput p3 = gpio.provisionDigitalOutputPin(
			RaspiPin.GPIO_29, "MyLED", PinState.LOW);

	@Override
	//called when all LEDs have to be turned off
	public void led_all_off() {
		p1.low();
		p2.low();
		p3.low();
	}

	@Override
	//called when all LEDs have to be turned on
	public void led_all_on() {
		p1.high();
		p2.high();
		p3.high();
	}

	@Override
	//called when all LEDs have to blink in a pattern to indicate an error
	public void led_error(int blink_count) throws InterruptedException {
		for (int i = 0; i < blink_count; i++) {
			p1.low();
			p2.low();
			p3.low();
			Thread.sleep(250);
			p1.high();
			p2.low();
			p3.low();
			Thread.sleep(250);
			p1.low();
			p2.high();
			p3.low();
			Thread.sleep(250);
			p1.low();
			p2.low();
			p3.high();
			Thread.sleep(250);
			p1.low();
			p2.high();
			p3.low();
			Thread.sleep(250);
			p1.high();
			p2.low();
			p3.low();
			Thread.sleep(250);
		}
		p1.low();
		p2.low();
		p3.low();
	}

	@Override
	// called to represent Blind State = CLOSE on LEDs
	public void led_when_low() {
		p1.low();
		p2.low();
		p3.high();
	}

	@Override
	// called to represent Blind State = HALF on LEDs
	public void led_when_mid() {
		p1.low();
		p2.high();
		p3.high();
	}

	@Override
	// called to represent Blind State = OPEN on LEDs
	public void led_when_high() {
		p1.high();
		p2.high();
		p3.high();
	}

	// reading ambient light reading
	@Override
	public int read_ambient_light_intensity() {
		// Refer RpiSensorViaMPC3008 for usage
		/*
		 * Reading ambient light from the photocell sensor using the MCP3008 ADC 
		 */
		int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int ambient = (int)(adc_ambient / 10.24); 
		
		return ambient;
	}

	// reading temperature reading
	@Override
	public int read_temperature() {
		// TODO Auto-generated method stub
		// Refer RpiSensorViaMPC3008 for usage
		/*
		 * Reading temperature from the TMP36 sensor using the MCP3008 ADC 
		 */
		int adc_temperature = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int temperature = (int)(adc_temperature / 10.24); 
		
		float tmp36_mVolts =(float) (adc_temperature * (3300.0/1024.0));
		// 10 mv per degree
        float temp_C = (float) (((tmp36_mVolts - 100.0) / 10.0) - 40.0);
		return (int) temp_C;
	}

}
